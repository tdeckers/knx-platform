package com.ducbase.knxplatform.devices;

public abstract class DimmedLight extends Switched {

	abstract public void setDimValue(Integer value);
	abstract public Integer getDimValue();
	
}
