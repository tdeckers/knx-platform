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
	
	private AtomicInteger idGen = new AtomicInteger(1);
	
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

	public Device getDevice(int intId) {
		for(Device device: devices) {
			if (device.getId() == intId) {
				return device;
			}
		}
		return null;
	}	
	
	public int addDevice(Device device) {
		int id = idGen.getAndIncrement();
		device.setId(id);
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
		return id;
	}
	
}
