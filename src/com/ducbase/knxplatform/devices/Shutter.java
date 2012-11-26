package com.ducbase.knxplatform.devices;


public abstract class Shutter extends Device {

	abstract public void up();
	abstract public void down();
	abstract public void stop();
	
	abstract public void setPosition(Integer position);
	abstract public Integer getPosition();
	
}
