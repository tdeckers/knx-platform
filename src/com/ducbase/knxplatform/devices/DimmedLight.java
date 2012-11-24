package com.ducbase.knxplatform.devices;

import org.codehaus.jettison.json.JSONObject;

import com.ducbase.knxplatform.adapters.devices.DeviceException;

public abstract class DimmedLight extends SwitchedLight {

	abstract public void setDimValue(Integer value);
	abstract public Integer getDimValue();

}
