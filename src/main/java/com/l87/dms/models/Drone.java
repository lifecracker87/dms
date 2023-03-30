package com.l87.dms.models;

public class Drone {
	public Long id;
	public String serialno;
	public String model;
	public int weightlimit;
	public int batterycapicity;
	public String state;
	
	public enum State {IDLE, LOADING, LOADED, DELIVERING, DELIVERED, RETURNING}
	public enum Model {Lightweight, Middleweight, Cruiserweight, Heavyweight}
	
	public int currentWt;
}
