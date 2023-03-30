package com.l87.dms.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.l87.dms.models.Drone;
import com.l87.dms.models.Medication;
import com.l87.dms.utils.DB;
import com.l87.dms.utils.DbResponse;
import com.l87.dms.utils.Messenger;

import jakarta.persistence.Tuple;


@Component
public class DroneService {
	
	@Autowired
	DB db;
	
	private String table = "drones";
	
	public ResponseEntity<Map<String, Object>> store(Drone model) {
		List<String> msgs = validate(model);
		if(msgs.size()>0) {
			return Messenger.getMessenger().setData(msgs).error();
		}
		DbResponse dbresp;
		String sql = "insert into " + table
				+ " (serialno,model,weightlimit,state) values (?,?,?,?)";
		dbresp = db.execute(sql, Arrays.asList(model.serialno,model.model,model.weightlimit,Drone.State.IDLE.name()));
		if (dbresp.getErrorNumber() == 0) {
			return Messenger.getMessenger().success();
		} else {
			return Messenger.getMessenger().error();
		}
	}
	
	public ResponseEntity<Map<String, Object>> load(Medication m) {
		List<String> msgs = validateMedicatins(m);
		if(msgs.size()>0) {
			return Messenger.getMessenger().setData(msgs).error();
		}
		//check if any drone is available
		String sql = "select * from "+table+" where state='"+Drone.State.IDLE.name()+"' and weightlimit>=(currentwt+"+m.weight+") and batterycapicity>25 order by weightlimit limit 1";
		Tuple t = db.getSingleResult(sql);
		if(t!=null) {
			//selected, let's hold it
			String droneid=t.get("id")+"";
			db.execute("update "+table+" set state='"+Drone.State.LOADING.name()+"' where id="+droneid);
			//load it
			db.execute("insert into medications(droneid,code,name,weight,image) values(?,?,?,?,?)",Arrays.asList(droneid,m.code,m.name,m.weight,m.image));
			int wtLimit = Integer.parseInt(t.get("weightlimit")+"");
			int curWt = Integer.parseInt(t.get("currentwt")+"");
			if(wtLimit>(curWt+m.weight)) {
				db.execute("update "+table+" set currentwt=(currentwt+"+m.weight+"),state='"+Drone.State.IDLE.name()+"' where id="+droneid);
			}else {
				db.execute("update "+table+" set currentwt=(currentwt+"+m.weight+"), state='"+Drone.State.LOADED.name()+"' where id="+droneid);
			}
			return Messenger.getMessenger().setMessage("Medication Loaded; Drone Serialno "+t.get("serialno")).success();
		}
		return Messenger.getMessenger().setMessage("Sorry no drones are available for now, try again some time later.").error();
		
	}
	
	private List<String> validateMedicatins(Medication m){
		List<String> msgs = new ArrayList<>();
		if(m.name==null) {
			msgs.add("Name is required.");
		}else {
			Pattern p = Pattern.compile("[a-zA-Z0-9_-]*");
			if(!p.matcher(m.name).find()) {
				msgs.add("Only alphanumeric with dash and underscore are valid.");
			}
		}
		if(m.weight>500) {
			msgs.add("Our drones can only take load upto 500g.");
		}
		if(m.code!=null) {
			Pattern p = Pattern.compile("[A-Z0-9_]*");
			if(!p.matcher(m.code).find()) {
				msgs.add("Only only upper case letters, underscore and numbers valid.");
			}
		}
		return msgs;
	}
	
	private List<String> validate(Drone d) {
		List<String> msgs = new ArrayList<>();
		if(d.serialno==null) {
			msgs.add("Serial No is required.");
		}else {
			if(d.serialno.length()>100) {
				msgs.add("Seraialno Must not contain more then 100 characters");
			}else {
				String count = db.getSingleResult("select count(id) as c from "+table+" where serialno=?",Arrays.asList(d.serialno)).get(0)+"";
				if(Integer.parseInt(count)>0) {
					msgs.add("Seraialno already exists.");
				}
			}
		}
		if(d.model==null) {
			msgs.add("Model is required.");
		}else {
			List<String> droneModels = Stream.of(Drone.Model.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());
			if(!droneModels.contains(d.model)) {
				msgs.add("Model Must be among "+String.join(", ",droneModels));
			}
		}
		if(d.weightlimit > 500) {
			msgs.add("Weightlimit cannot be more then 500.");
		}
		return msgs;
	}
	
	public ResponseEntity<Map<String, Object>> availableDrones() {
		String sql = "select serialno,model,weightlimit,currentwt from "+table+" where state=?";
		List<Tuple> tl = db.getResultList(sql,Arrays.asList(Drone.State.IDLE.name()));
		if(tl.size()>0) {
			List<Map<String,Object>> data = new ArrayList<>();
			for(Tuple t:tl) {
				data.add(Map.of("serialno",t.get("serialno"),"model",t.get("model"),"weightlimit",t.get("weightlimit")+"g","currentload",t.get("currentwt")+"g"));
			}
			return Messenger.getMessenger().setData(data).success();
		}
		return Messenger.getMessenger().setMessage("No Drones are available for now.").success();
	}
	
	public ResponseEntity<Map<String, Object>> batteryLevel(String serialno) {
		String sql = "select serialno,model,batterycapicity from "+table+" where serialno=?";
		Map<String,Object> tl = db.getSingleResultMap(sql,Arrays.asList(serialno));
		if(tl!=null) {
			tl.put("BATTERYCAPICITY",tl.get("BATTERYCAPICITY")+"%");
			return Messenger.getMessenger().setData(tl).success();
		}
		return Messenger.getMessenger().setMessage("No Such Drone Exists.").error();
	}
	
	public ResponseEntity<Map<String, Object>> loadedMedications(String serialno) {
		String sql = "select m.name,m.code,m.weight,m.image from medications m join "+table+" d on d.id=m.droneid  where d.serialno=?";
		List<Map<String,Object>> tl = db.getResultListMap(sql,Arrays.asList(serialno));
		if(tl.size()>0) {
			return Messenger.getMessenger().setData(tl).success();
		}
		return Messenger.getMessenger().setMessage("No medications Found.").success();
	}

	public ResponseEntity<Map<String, Object>> chargeAllDrones() {
		String sql="update drones set batterycapicity=100 where state in ('"+Drone.State.IDLE.name()+"','"+Drone.State.LOADING.name()+"','"+Drone.State.LOADED.name()+"')";
		db.execute(sql);
		return Messenger.getMessenger().setMessage("All Available Drones Charged Successfully.").success();
	}
	
}
