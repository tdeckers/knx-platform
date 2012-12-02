package com.ducbase.knxplatform.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.ducbase.knxplatform.adapters.devices.KNXDimmedLight;
import com.ducbase.knxplatform.adapters.devices.KNXShutter;
import com.ducbase.knxplatform.adapters.devices.KNXSwitched;
import com.ducbase.knxplatform.adapters.devices.KNXTemperatureSensor;
import com.ducbase.knxplatform.adapters.devices.KNXThermostat;
import com.ducbase.knxplatform.devices.DeviceManager;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONUnmarshaller;

public class ConfigManager {
	
	private static final Logger logger = Logger.getLogger(ConfigManager.class.getName());
	private static final String configFilename = "devices.json";
	
	public ConfigManager() {
		
	}
	
	public static void loadDevices() throws JAXBException, FileNotFoundException {
		JSONJAXBContext context = new JSONJAXBContext(Config.class);
		JSONUnmarshaller um = context.createJSONUnmarshaller();
				
		File configFile = new File(configFilename);
		logger.fine("Using config file: " + configFile.getAbsolutePath());		
		FileReader reader = new FileReader(configFile);
		Config config = (Config) um.unmarshalFromJSON(reader, Config.class);
		
		DeviceManager manager = DeviceManager.getInstance();
		logger.info("Devices found: " + config.devices.size());
		
		for (ConfigDevice device: config.devices) {
			logger.fine("Loading device " + device.toString());
			
			// TBD: find class from device.type and do smart things with it.
			
			if ("switched".equalsIgnoreCase(device.type)) {
				KNXSwitched switched = new KNXSwitched(device.id, device.name, device.gstatus, device.gswitch);
				switched.setDescription(device.description);
				manager.addDevice(switched);
			}
			if ("dimmedlight".equalsIgnoreCase(device.type)) {
				KNXDimmedLight light = new KNXDimmedLight(device.id, device.name, device.gstatus, device.gswitch, device.gdimr, device.gdimw);
				light.setDescription(device.description);
				manager.addDevice(light);
			}			
			if ("tempSensor".equalsIgnoreCase(device.type)) {
				KNXTemperatureSensor sensor = new KNXTemperatureSensor(device.id, device.name, device.gactual);
				sensor.setDescription(device.description);
				manager.addDevice(sensor);
			}
			if ("thermostat".equalsIgnoreCase(device.type)) {
				KNXThermostat thermostat = new KNXThermostat(device.id, device.name, device.gactual, device.gsetpointr, device.gsetpointw, device.gvariable, device.gmoder, device.gmodew);
				thermostat.setDescription(device.description);
				manager.addDevice(thermostat);				
			}
			if ("shutter".equalsIgnoreCase(device.type)) {
				KNXShutter shutter = new KNXShutter(device.id, device.name, device.gupdown, device.gstop, device.gposr, device.gposw);
				shutter.setDescription(device.description);
				manager.addDevice(shutter);				
			}
		}
	
	}
	
	
}
