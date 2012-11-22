package com.ducbase.knxplatform.devices;

public abstract class Thermostat extends TemperatureSensor {

	abstract public void setSetpoint(Float temperature);
	abstract public Float getSetpoint();
	/**
	 * set the heating mode. 
	 * 
	 * 0 = Auto
	 * 1 = Comfort
	 * 2 = Standby
	 * 3 = Economy
	 * 4 = Building Protection
	 * 5 … 255 = reserved
	 * 
	 * @param mode
	 */
	abstract public void setMode(Integer mode);
	abstract public Integer getMode();
	abstract public Integer getVariable();
	
}
