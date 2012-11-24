package com.ducbase.knxplatform.adapters.devices;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jettison.json.JSONObject;

import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.devices.BooleanStatus;

@XmlRootElement
public class KNXBooleanStatus extends BooleanStatus implements KNXDevice {
	
	private KNXAdapter adapter;
	
	private String stateGroup;
	private DPT stateGroupType = DPTXlatorBoolean.DPT_BOOL;
	
	public KNXBooleanStatus() {
		
	}
	
	public KNXBooleanStatus(String id, String name, String stateGroup) {
		this.setId(id);
		this.setName(name);
		
		this.stateGroup = stateGroup;
		
		this.adapter = KNXAdapter.getInstance();
	}

	@Override
	public String[] getListenGroups() {
		return new String[] { this.stateGroup };
	}

	@Override
	public Map<String, DPT> getTypeMap() {
		Map<String, DPT> map = new HashMap<String, DPT>();
		map.put(this.stateGroup, this.stateGroupType);
		return map;
	}

	@XmlElement
	@Override
	public Boolean isOn() {
		String stateString = adapter.getValueForGroupAddress(stateGroup);
		if ("true".equalsIgnoreCase(stateString)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void update(JSONObject object) throws DeviceException {
		throw new DeviceException("Read Only type. No updates please.");
	}

}
