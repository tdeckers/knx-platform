package com.ducbase.knxplatform.adapters.devices;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat;
import tuwien.auto.calimero.dptxlator.DPTXlator2ByteUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlator4ByteUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.devices.Thermostat;

@XmlRootElement
public class KNXThermostat extends Thermostat implements KNXDevice {

	private KNXAdapter adapter;
	
	private String actualGroup;
	private DPT actualGroupType = DPTXlator2ByteFloat.DPT_TEMPERATURE;
	
	private String setPointReadGroup;
	private DPT setPointReadGroupType = DPTXlator2ByteFloat.DPT_TEMPERATURE;
	
	private String setPointWriteGroup;
	private DPT setPointWriteGroupType = DPTXlator2ByteFloat.DPT_TEMPERATURE;	
	
	private String variableGroup;
	private DPT variableGroupType = DPTXlator8BitUnsigned.DPT_SCALING;
	
	private String modeGroup;
	// TODO: this is actually not correct.
	// Should be DPT 20.102 instead.
	private DPT modeGroupType = DPTXlator8BitUnsigned.DPT_DECIMALFACTOR; 

	public KNXThermostat() {
		
	}
	
	public KNXThermostat(String id, String name, String actualGroup, String setPointReadGroup, String setPointWriteGroup, String variableGroup, String modeGroup) {
		this.setId(id);
		this.setName(name);
		this.actualGroup = actualGroup;
		this.setPointReadGroup = setPointReadGroup;
		this.setPointWriteGroup = setPointWriteGroup;
		this.variableGroup = variableGroup;
		this.modeGroup = modeGroup;
		
		this.adapter = KNXAdapter.getInstance();		
	}
	
	@Override
	public String[] getListenGroups() {
		return new String[] { this.actualGroup, this.setPointReadGroup, this.variableGroup, this.modeGroup };
	}

	@Override
	public Map<String, DPT> getTypeMap() {
		Map<String, DPT> map = new HashMap<String, DPT>();
		map.put(this.actualGroup, this.actualGroupType);
		map.put(this.setPointReadGroup, this.setPointReadGroupType);
		map.put(this.setPointWriteGroup, this.setPointWriteGroupType);
		map.put(this.variableGroup, this.variableGroupType);
		map.put(this.modeGroup, this.modeGroupType);
		return map;
	}

	@Override
	public void setSetpoint(Float temperature) {
		adapter.sendFloat(setPointWriteGroup, temperature);
	}

	@XmlElement
	@Override
	public Float getSetpoint() {
		String setpointString = adapter.getValueForGroupAddress(setPointReadGroup);
		float setpoint = 0;
		try {
			setpoint = Float.parseFloat(setpointString);
		} catch (NumberFormatException|NullPointerException e) {
			// ignore, we've initialized to 0;
		}
		return setpoint; 
	}

	@Override
	public void setMode(Integer mode) {
		adapter.sendIntUnscaled(modeGroup, mode);
	}

	@XmlElement
	@Override
	public Integer getMode() {
		String modeString = adapter.getValueForGroupAddress(modeGroup);
		int mode = -1;
		try {
			mode = Integer.parseInt(modeString);
		} catch (NumberFormatException e) {
			// ignore, we've initialized with -1 for this case.
		}
		return mode;
	}

	@XmlElement
	@Override
	public Integer getVariable() {
		String variableString = adapter.getValueForGroupAddress(variableGroup);
		int variable = -1;
		try {
			variable = Integer.parseInt(variableString); 
		} catch (NumberFormatException e) {
			// ignore, we've initialized with -1 for this case.
		}
		return variable;
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
		float setpoint;		
		try {
			setpoint = new Double(object.getDouble("setpoint")).floatValue();  // this is how the bean property is called (see this class)
			this.setSetpoint(setpoint);
		} catch (JSONException e) {
			// getDouble throws a JSONException is not found.
			// throw new DeviceException("property 'setpoint' not found", e);
			// We'll just skip the update if not found
		} 
		int mode;
		try {
			mode = object.getInt("mode");  // this is how the bean property is called (see this class)
			this.setMode(mode);
		} catch (JSONException e) {
			// getInt throws a JSONException is not found.
			// We'll just skip the update if not found
		} 		
	}

}
