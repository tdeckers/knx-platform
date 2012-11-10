package com.ducbase.knxplatform.adapters.devices;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jettison.json.JSONObject;

import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat;
import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.devices.TemperatureSensor;

@XmlRootElement
public class KNXTemperatureSensor extends TemperatureSensor implements
		KNXDevice {
	
	private KNXAdapter adapter;
	
	private String stateGroup;
	private DPT stateGroupType = DPTXlator2ByteFloat.DPT_TEMPERATURE;	
	
	public KNXTemperatureSensor() {
		
	}
	
	public KNXTemperatureSensor(String name, String stateGroup) {
		this.setName(name);
		this.stateGroup = stateGroup;
		
		this.adapter = KNXAdapter.getInstance();				
	}

	@Override
	public String[] getListenGroups() {
		return new String[] { this.stateGroup };
	}

	public String getStateGroup() {
		return stateGroup;
	}

	public void setStateGroup(String stateGroup) {
		this.stateGroup = stateGroup;
	}

	@Override
	public Map<String, DPT> getTypeMap() {
		Map<String, DPT> map = new HashMap<String, DPT>();
		map.put(this.stateGroup, this.stateGroupType);
		return map;

	}

	@XmlElement
	@Override
	public Float getTemperature() {
		String stateString = adapter.getValueForGroupAddress(stateGroup);
		return Float.parseFloat(stateString);
	}

	@Override
	public void update(JSONObject object) throws DeviceException {
		// Can't update... Ignoring.
	}

}
