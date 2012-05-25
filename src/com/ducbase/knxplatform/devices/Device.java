package com.ducbase.knxplatform.devices;

/**
 * 
 * @author tom@ducbase.com
 *
 */
public abstract class Device {
	
	private String name;
	private String description;
	
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
	public Object getStatus() {
		return status;
	}
	public void setStatus(Object status) {
		this.status = status;
	}
	private Object status;

}
