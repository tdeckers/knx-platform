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
import com.ducbase.knxplatform.devices.Switched;


@XmlRootElement
public class KNXSwitched extends Switched implements KNXDevice {

	private KNXAdapter adapter;
	
	private String stateGroup;
	private DPT stateGroupType = DPTXlatorBoolean.DPT_SWITCH;
	
	private String switchGroup;
	private DPT switchGroupType = DPTXlatorBoolean.DPT_SWITCH;
	
	private boolean canRead = false;
	private boolean canWrite = false;
	
	public KNXSwitched() {		
	}
	
	public KNXSwitched(String id, String name, String stateGroup, String switchGroup) {
		this.setId(id);
		this.setName(name);
		this.stateGroup = stateGroup;
		if (this.stateGroup != null && !"".equals(this.stateGroup)) {
			canRead = true;
		}
		this.switchGroup = switchGroup;
		if (this.switchGroup != null && !"".equals(this.switchGroup)) {
			canWrite = true;
		}
		
		if ( (!canRead) && (!canWrite)) {
			throw new RuntimeException("Either stategroup or switchgroup must be configured");
		}
		
		this.adapter = KNXAdapter.getInstance();
	}		
	
	@Override
	public void turnOn() {	
		if (!this.isReadonly()) {
			adapter.sendBoolean(switchGroup, true);
		}
	}

	@Override
	public void turnOff() {
		if (!this.isReadonly()) {
			adapter.sendBoolean(switchGroup, false);
		}
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
		if (this.isWriteonly()) {
			throw new RuntimeException(this.getId() + " is write only.");
		}
		
		String stateString = adapter.getValueForGroupAddress(stateGroup);
		if ("on".equalsIgnoreCase(stateString)) {
			return true;
		} else {
			return false;
		}
	}
	
	@XmlElement
	@Override
	public Boolean isReadonly() {
		return canRead && !canWrite;
	}

	@XmlElement
	@Override
	public Boolean isWriteonly() {
		return canWrite && !canRead;
	}	

	@Override
	public String[] getListenGroups() {
		if (!this.isWriteonly()) {
			return new String[] { this.stateGroup };
		} else {
			return new String[] {};
		}
	}
	
	public Map<String, DPT> getTypeMap() {
		Map<String, DPT> map = new HashMap<String, DPT>();
		if (!this.isWriteonly()) {
			map.put(this.stateGroup, this.stateGroupType);
		}
		if (!this.isReadonly()) {
			map.put(this.switchGroup, this.switchGroupType);
		}
		return map;
	}

	@Override
	public void update(JSONObject object) throws DeviceException {
		if (this.isWriteonly()) {
			return; // no updates expected!
		}
		
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
