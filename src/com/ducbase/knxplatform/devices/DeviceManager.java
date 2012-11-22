package com.ducbase.knxplatform.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import tuwien.auto.calimero.datapoint.Datapoint;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.adapters.devices.KNXDevice;

public class DeviceManager {
	
	List<Device> devices = new ArrayList<Device>();
	
	private static DeviceManager instance;
			
	private DeviceManager() {

	}
	
	public static DeviceManager getInstance() {
		if (instance == null) {
			synchronized(DeviceManager.class) {
				if (instance == null) {
					instance = new DeviceManager();
				}
			}			
		}
		return instance;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public int getDeviceCount() {
		return devices.size();
	}

	public Device getDevice(String id) {
		for(Device device: devices) {
			if (device.getId()!= null && device.getId().equals(id)) {
				return device;
			}
		}
		return null;
	}	
	
	public String addDevice(Device device) {
		devices.add(device);
		if (device instanceof KNXDevice) {
			KNXDevice knxDevice = (KNXDevice) device;
			KNXAdapter adapter = KNXAdapter.getInstance();
			// Get mapping from device and add to the KNX adapter
			adapter.addTypeMap(knxDevice.getTypeMap());
			
			// register groups for which we listen on KNX bus
			String[] listenGroups = knxDevice.getListenGroups();
			adapter.registerListenFor(listenGroups, device.getId());
		}
		return device.getId();
	}
	
}
