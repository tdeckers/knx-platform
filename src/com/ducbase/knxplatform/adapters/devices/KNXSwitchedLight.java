package com.ducbase.knxplatform.adapters.devices;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.devices.SwitchedLight;


@XmlRootElement
public class KNXSwitchedLight extends SwitchedLight implements KNXDevice {

	private KNXAdapter adapter;
	
	private String stateGroup;
	private DPT stateGroupType = DPTXlatorBoolean.DPT_SWITCH;
	
	private String switchGroup;
	private DPT switchGroupType = DPTXlatorBoolean.DPT_SWITCH;
	
	public KNXSwitchedLight() {		
	}
	
	public KNXSwitchedLight(String id, String name, String stateGroup, String switchGroup) {
		this.setId(id);
		this.setName(name);
		this.stateGroup = stateGroup;
		this.switchGroup = switchGroup;
		
		this.adapter = KNXAdapter.getInstance();
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

	@Override
	public String[] getListenGroups() {
		return new String[] { this.stateGroup };
	}
	
	public Map<String, DPT> getTypeMap() {
		Map<String, DPT> map = new HashMap<String, DPT>();
		map.put(this.stateGroup, this.stateGroupType);
		map.put(this.switchGroup, this.switchGroupType);
		return map;
	}

	@Override
	public void update(JSONObject object) throws DeviceException {
		boolean on;
		try {
			 on = object.getBoolean("on");  // this is how the bean property is called (see this class)
		} catch (JSONException e) {
			// getBoolean throws a JSONException is not found.
			throw new DeviceException("property 'on' not found", e);
		} 
		if (on) {
			this.turnOn();
		} else {
			this.turnOff();
		}
	}

}
