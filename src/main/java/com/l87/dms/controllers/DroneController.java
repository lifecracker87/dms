package com.l87.dms.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.l87.dms.models.Drone;
import com.l87.dms.models.Medication;
import com.l87.dms.services.DroneService;

@RestController
@RequestMapping("drone")
public class DroneController {
	
	@Autowired
	DroneService ds;
	
	@PostMapping("register")
	public ResponseEntity<Map<String,Object>> registerDrone(@RequestBody Drone drone){
		return ds.store(drone);
	}
	
	@PostMapping("load")
	public ResponseEntity<Map<String,Object>> load(@RequestBody Medication m){
		return ds.load(m);
	}
	
	@GetMapping("available")
	public ResponseEntity<Map<String,Object>> availableDrones(){
		return ds.availableDrones();
	}
	
	@GetMapping("battery-level/{serialno}")
	public ResponseEntity<Map<String,Object>> batteryLevel(@PathVariable String serialno){
		return ds.batteryLevel(serialno);
	}
	
	@GetMapping("loaded-medications/{serialno}")
	public ResponseEntity<Map<String,Object>> loadedMedications(@PathVariable String serialno){
		return ds.loadedMedications(serialno);
	}
	
	@GetMapping("recharge-all")
	public ResponseEntity<Map<String,Object>> chargeAllDrones(){
		return ds.chargeAllDrones();
	}
	
	
}