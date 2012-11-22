package com.ducbase.knxplatform.devices;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jettison.json.JSONObject;

import com.ducbase.knxplatform.adapters.devices.DeviceException;


/**
 * 
 * @author tom@ducbase.com
 *
 */
@XmlRootElement
public abstract class Device {
	
	private String id;
	private String name;
	private String description;
	private String type;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
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
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

	public String toString() {
		return "Device: " + name;
	}
	
	/**
	 * method used by REST api to update device.
	 * 
	 * @param object
	 * @throws DeviceException
	 */
	abstract public void update(JSONObject object) throws DeviceException;
	
}
