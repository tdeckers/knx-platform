package com.ducbase.knxplatform.adapters.devices;

import java.util.Map;

import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.dptxlator.DPT;

public interface KNXDevice {
	
	public String[] getListenGroups();
	
	public Map<String, DPT> getTypeMap();

}
