package com.ducbase.knxplatform.devices;

/**
 * representing a switched light.
 * 
 * @author tom@ducbase.com
 *
 */
public abstract class SwitchedLight extends Device {

	abstract public Boolean isOn();
	abstract public void turnOn();
	abstract public void turnOff();
	abstract public void toggle();
	
	public String getType() {
		return SwitchedLight.class.getSimpleName();
	}

}
