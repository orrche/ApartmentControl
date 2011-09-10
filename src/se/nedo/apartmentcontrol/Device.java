package se.nedo.apartmentcontrol;

import java.util.LinkedList;
import java.util.List;

public class Device {
	String name;
	String type;
	boolean state;
	List<Integer> widgets;
	int myid;
	
	public Device(int id){
		state = false;
		myid = id;
		name = "";
		type = ""; 
		widgets = new LinkedList<Integer>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean setState(boolean state) {
		if ( this.state == state )
			return false;
		this.state = state;
		return true;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean getState() {
		return state;
	}
}
