package com.ducbase.knxplatform.adapters.devices;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator4ByteUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.devices.DimmedLight;

@XmlRootElement
public class KNXDimmedLight extends DimmedLight implements KNXDevice {

	private KNXAdapter adapter;
	
	private String stateGroup;
	private DPT stateGroupType = DPTXlatorBoolean.DPT_SWITCH;
	
	private String switchGroup;
	private DPT switchGroupType = DPTXlatorBoolean.DPT_SWITCH;
	
	private String dimReadGroup;
	private DPT dimReadGroupType = DPTXlator8BitUnsigned.DPT_PERCENT_U8; //TODO change to scaling var!
	
	private String dimWriteGroup;
	private DPT dimWriteGroupType = DPTXlator8BitUnsigned.DPT_PERCENT_U8;
	
	public KNXDimmedLight() {
	}
	
	public KNXDimmedLight(String id, String name, String stateGroup, String switchGroup, String dimReadGroup, String dimWriteGroup) {
		this.setId(id);
		this.setName(name);
		this.stateGroup = stateGroup;
		this.switchGroup = switchGroup;
		this.dimReadGroup = dimReadGroup;
		this.dimWriteGroup = dimWriteGroup;
		
		this.adapter = KNXAdapter.getInstance();
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
	public void setDimValue(Integer value) {
		int knxValue = (int) (value * 2.55); // dimValue is in %, KNX value is 0-255.
		adapter.sendIntUnscaled(this.dimWriteGroup, knxValue);
	}

	@XmlElement
	@Override
	public Integer getDimValue() {
		int retVal = -1;
		String valueString = adapter.getValueForGroupAddress(this.dimReadGroup);
		try {
			int tmp = Integer.parseInt(valueString);
			retVal = (int) (tmp / 2.55);  // dimValue is in %, KNX value is 0-255.
		} catch (NumberFormatException e) {
			// ignore, we've initialized value to -1 for this situation.
		}
		return retVal;
	}		
	
	@Override
	public String[] getListenGroups() {
		return new String[] { this.stateGroup, this.dimReadGroup };
	}

	@Override
	public Map<String, DPT> getTypeMap() {
		Map<String, DPT> map = new HashMap<String, DPT>();
		map.put(this.stateGroup, this.stateGroupType);
		map.put(this.switchGroup, this.switchGroupType);
		map.put(this.dimReadGroup, this.dimReadGroupType);
		map.put(this.dimWriteGroup, this.dimWriteGroupType);
		return map;
	}

	@Override
	public void update(JSONObject object) throws DeviceException {
		boolean on;
		try {
			on = object.getBoolean("on");  // this is how the bean property is called (see this class)
			if (on) {
				this.turnOn();
			} else {
				this.turnOff();
			}

		} catch (JSONException e) {
			// getBoolean throws a JSONException is not found.
			// Fail silently if on is not found.
		} 
		
		int dimValue;
		try {
			dimValue = object.getInt("dimValue");  // this is how the bean property is called (see this class)
			this.setDimValue(dimValue);
		} catch (JSONException e) {
			// getInt throws a JSONException is not found.
			// We'll just skip the update if not found
		} 				
	}

}
