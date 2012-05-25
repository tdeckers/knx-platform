package com.ducbase.knxplatform.devices;

/**
 * 
 * @author tom@ducbase.com
 *
 */
public abstract class Device {
	
	private String name;
	private String description;
	private State state;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	abstract public State getState();
	abstract public void setState(State state);

	public String toString() {
		return "Device: " + name;
	}
	
}
