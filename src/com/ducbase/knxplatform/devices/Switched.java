package com.ducbase.knxplatform.devices;

/**
 * representing a switched light.
 * 
 * @author tom@ducbase.com
 *
 */
public abstract class Switched extends Device {

	abstract public Boolean isOn();
	abstract public Boolean isReadonly();
	abstract public Boolean isWriteonly();
	abstract public void turnOn();
	abstract public void turnOff();
	abstract public void toggle();
	
}
