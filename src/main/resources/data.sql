insert into drones (serialno,model,weightlimit,state) values ('U435Y','Lightweight','300','IDLE');
insert into drones (serialno,model,weightlimit,state) values ('12345','Lightweight','300','IDLE');
insert into drones (serialno,model,weightlimit,state) values ('1234','Lightweight','300','IDLE');

insert into medications (droneid,code,name,weight,image) values((select id from drones where serialno='1234'),'PCTM','Paracetamol IP 500Mg','20','pcm.png');
update drones set currentwt=20 where serialno='1234';