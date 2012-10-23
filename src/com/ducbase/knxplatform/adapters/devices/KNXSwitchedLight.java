package com.ducbase.knxplatform.adapters.devices;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.devices.SwitchedLight;


@XmlRootElement
public class KNXSwitchedLight extends SwitchedLight {

	private KNXAdapter adapter;
	private String stateGroup;
	private String switchGroup;
	
	public KNXSwitchedLight() {		
	}
	
	public KNXSwitchedLight(String stateGroup, String switchGroup) {
		this.stateGroup = stateGroup;
		this.switchGroup = switchGroup;
		
		this.adapter = KNXAdapter.getInstance();
		adapter.registerBooleanGroup(stateGroup);
	}	
	
	public String getStateGroup() {
		return stateGroup;
	}

	public void setStateGroup(String stateGroup) {
		this.stateGroup = stateGroup;
	}

	public String getSwitchGroup() {
		return switchGroup;
	}

	public void setSwitchGroup(String switchGroup) {
		this.switchGroup = switchGroup;
	}

	@Override
	public void turnOn() {
		adapter.sendBoolean(switchGroup, true);
	}

	@Override
	public void turnOff() {
		adapter.sendBoolean(switchGroup, false);
	}

	@Override
	public void toggle() {
		if (this.isOn()) {
			this.turnOff();
		} else {
			this.turnOn();
		}
	}

	@XmlElement
	@Override
	public Boolean isOn() {
		String stateString = adapter.getValueForGroupAddress(stateGroup);
		if ("on".equalsIgnoreCase(stateString)) {
			return true;
		} else {
			return false;
		}
	}

}
