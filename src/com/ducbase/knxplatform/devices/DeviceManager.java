package com.ducbase.knxplatform.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.ducbase.knxplatform.adapters.devices.KNXSwitchedLight;

public class DeviceManager {
	
	List<Device> devices = new ArrayList<Device>();
	
	private static DeviceManager instance;
	
	private AtomicInteger idGen = new AtomicInteger(1);
	
	private DeviceManager() {
		// TODO change with reading of configuration
		SwitchedLight light = new KNXSwitchedLight("1/1/45", "1/4/45");
		light.setId(idGen.getAndIncrement());
		light.setName("Light of my life");
		light.setDescription("This is a light that shines.");
		devices.add(light);
		SwitchedLight light2 = new KNXSwitchedLight("1/1/44", "1/4/44");
		light2.setId(idGen.getAndIncrement());
		light2.setName("Second light of my life");
		light2.setDescription("This is a light that doesn't shines.");
		devices.add(light2);
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
	
}
