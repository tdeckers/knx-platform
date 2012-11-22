package com.ducbase.knxplatform.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.adapters.devices.KNXSwitchedLight;
import com.ducbase.knxplatform.adapters.devices.KNXTemperatureSensor;
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
			logger.info("Loading device " + device.toString());
			
			// TBD: find class from device.type and do smart things with it.
			
			if ("switchedlight".equalsIgnoreCase(device.type)) {
				KNXSwitchedLight light = new KNXSwitchedLight(device.id, device.name, device.statusGroup, device.switchGroup);
				light.setDescription(device.description);
				manager.addDevice(light);
			}
			if ("tempSensor".equalsIgnoreCase(device.type)) {
				KNXTemperatureSensor sensor = new KNXTemperatureSensor(device.id, device.name, device.statusGroup);
				sensor.setDescription(device.description);
				manager.addDevice(sensor);
			}
		}
	
	}
	
	
}