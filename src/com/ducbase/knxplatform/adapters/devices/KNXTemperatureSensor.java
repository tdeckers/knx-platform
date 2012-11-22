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
	
	private String actualGroup;
	private DPT actualGroupType = DPTXlator2ByteFloat.DPT_TEMPERATURE;	
	
	public KNXTemperatureSensor() {
		
	}
	
	public KNXTemperatureSensor(String id, String name, String stateGroup) {
		this.setId(id);
		this.setName(name);
		this.actualGroup = stateGroup;
		
		this.adapter = KNXAdapter.getInstance();				
	}

	@Override
	public String[] getListenGroups() {
		return new String[] { this.actualGroup };
	}

	public String getStateGroup() {
		return actualGroup;
	}

	public void setStateGroup(String stateGroup) {
		this.actualGroup = stateGroup;
	}

	@Override
	public Map<String, DPT> getTypeMap() {
		Map<String, DPT> map = new HashMap<String, DPT>();
		map.put(this.actualGroup, this.actualGroupType);
		return map;

	}

	@XmlElement
	@Override
	public Float getTemperature() {
		String actualString = adapter.getValueForGroupAddress(actualGroup);
		float temp = 0;
		try {
			temp = Float.parseFloat(actualString);
		} catch (NumberFormatException|NullPointerException e) {
			// ignore, we've initialized to 0;
		}
		return temp;
	}

	@Override
	public void update(JSONObject object) throws DeviceException {
		// Can't update... Ignoring.
	}

}
