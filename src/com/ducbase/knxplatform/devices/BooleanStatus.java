package com.ducbase.knxplatform.devices;

import org.codehaus.jettison.json.JSONObject;

import com.ducbase.knxplatform.adapters.devices.DeviceException;

public abstract class BooleanStatus extends Device {

	abstract public Boolean isOn();
	
}
