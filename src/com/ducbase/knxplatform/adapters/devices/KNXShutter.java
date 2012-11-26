package com.ducbase.knxplatform.adapters.devices;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.devices.Shutter;

@XmlRootElement
public class KNXShutter extends Shutter implements KNXDevice {
	
	private KNXAdapter adapter;
	
	private String upDownGroup;
	private DPT upDownGroupType = DPTXlatorBoolean.DPT_UPDOWN; // 0 for up, 1 for down.
	
	private String stopGroup;
	private DPT stopGroupType = DPTXlatorBoolean.DPT_START; // 0 for stop, 1 for start.
	
	private String posReadGroup;
	private DPT posReadGroupType = DPTXlator8BitUnsigned.DPT_SCALING; // 0-100 %
	
	private String posWriteGroup;
	private DPT posWriteGroupType = DPTXlator8BitUnsigned.DPT_SCALING; // 0-100 %

	public KNXShutter() {
		
	}
	
	public KNXShutter(String id, String name, String upDownGroup, String stopGroup, String posReadGroup, String posWriteGroup) {
		this.setId(id);
		this.setName(name);
		this.upDownGroup = upDownGroup;
		this.stopGroup = stopGroup;
		this.posReadGroup = posReadGroup;
		this.posWriteGroup = posWriteGroup;
		
		this.adapter = KNXAdapter.getInstance();
	}
	
	@Override
	public String[] getListenGroups() {
		return new String[] { this.posReadGroup };
	}

	@Override
	public Map<String, DPT> getTypeMap() {
		Map<String, DPT> map = new HashMap<String, DPT>();
		map.put(this.upDownGroup, this.upDownGroupType);
		map.put(this.stopGroup, this.stopGroupType);
		map.put(this.posReadGroup, this.posReadGroupType);
		map.put(this.posWriteGroup, this.posWriteGroupType);
		return map;
	}

	@Override
	public void up() {
		adapter.sendBoolean(upDownGroup, false);

	}

	@Override
	public void down() {
		adapter.sendBoolean(upDownGroup, true);

	}
	
	@XmlElement
	/**
	 * bean method that allows using JAXB to command up/down.
	 */
	public void setUpDown(String upDown) {
		if ("up".equalsIgnoreCase(upDown)) {
			this.up();
		} else if ("down".equalsIgnoreCase(upDown)) {
			this.down();
		} else if ("stop".equalsIgnoreCase(upDown)) {
			this.stop();
		}
	}
	
	public String getUpDown() { return "n/a"; } // Dummy. Making JAXB happy.
	// [com.sun.istack.internal.SAXException2: The property has a setter "public void com.ducbase.knxplatform.adapters.devices.KNXShutter.setStop(java.lang.Boolean)" but no getter. For marshaller, please define getters.	
	

	@Override
	public void stop() {
		adapter.sendBoolean(stopGroup, false);

	}

	@Override
	public void setPosition(Integer position) {
		adapter.sendIntScaled(posWriteGroup, position);
	}

	@XmlElement
	@Override
	public Integer getPosition() {
		int retVal = -1;
		String valueString = adapter.getValueForGroupAddress(this.posReadGroup);
		try {
			retVal = Integer.parseInt(valueString);
		} catch (NumberFormatException e) {
			// ignore, we've initialized value to -1 for this situation.
		}
		return retVal;	}

	@Override
	public void update(JSONObject object) throws DeviceException {
		int position;
		try {
			position = object.getInt("position");  // this is how the bean property is called (see this class)
			this.setPosition(position);
		} catch (JSONException e) {
			// getInt throws a JSONException is not found.
			// We'll just skip the update if not found
		} 
		
		String upDown;
		try {
			upDown = object.getString("upDown");
			this.setUpDown(upDown);
		} catch (JSONException e) {
			// ignore. TODO: send bad request if error.
		}

	}

}
