Drone Management System:
As per the document Drones.pdf and it's requirements:

It is a Spring boot web application, with H2 database embeded.
Schema and sample Data files are already present in project can be viewed on files data.sql and schema.sql from resources

To run Application:
1. git clone https://github.com/lifecracker87/dms.git
2. Import porject to eclipse or or any other ide
3. Rum DmsApplication.java as Java Application
It will run on default port 8080 and accessable from http://localhost:8080

Available Apis:
Header:Content-Type: application/js

POST /drone/register, here we can register drones
GET /drone/available, here all available drones are listed
GET /drone/battery-level/{serialno}, get status of battery % for given drone, identifier being serialno
GET /drone/loaded-medications/{serialno}, get all loaded medications for given drone, identifier being serialno
GET /drone/recharge-all, recharges all available(State being:idle,loading,loaded)the drones to their full battery capicity 100%, Before loading this api should be called, on initilization all drones are assumed to be with no charge
POST /drone/load, here new medications can be loaded, image for now are assumed to be the path only

Except for exceptions: All responses are formatted as below
{
	status:1,//0->for failure,1->success
	message:"<Message from Server>",
	data:"" //Data response form server, may vary per operation, it may also contain validation messages if occured any
}

As for now, the states other then idle,loading,loaded are not implemented

For testign purpose we can use serialno 1234, for battery level and loaded medications



