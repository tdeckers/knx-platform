package com.ducbase.knxplatform.adapters;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat;
import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.event.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.ducbase.knxplatform.config.DeviceConfig;
import com.ducbase.knxplatform.config.DevicesConfig;
import com.ducbase.knxplatform.devices.Device;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * abstraction layer for the KNX interface.
 * 
 * This class will:
 * <li>keep a map of all KNX devices to manage/monitor</li>
 * <li>keep a map of group addresses and the device properties they map to</li>
 * 
 * TODO:
 * <li>This class should probably by a subclass of Adapter (to create)</li>
 * 
 * @author tom@ducbase.com
 *
 */
public class KNXAdapter {

	static Logger logger = Logger.getLogger(KNXAdapter.class.getName());
	
	static final String CACHE_NAME = "knx-cache";
	List<String> groupAddresses = new ArrayList<String>();
	
	/**
	 * a KNXAdapter maintains a cache of group address states.  Mainly for those group addresses used for Device state's.
	 */
	private Cache cache;

	private KNXMonitor monitor;
	
	/**
	 * Create a KNXAdapter
	 * 
	 * TODO: make singleton
	 */
	public KNXAdapter() {
		// TODO: centralize reading from config and configuring system... this to catch config issue in one place.
		
		FileReader reader = null;
		try {
			// Create reader for config file
			reader = new FileReader("devices.json");
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
				
		// Using Google Gson for parsing.
		Gson json = new Gson();
		// Map JSON file to DevicesConfig object
		DevicesConfig config = json.fromJson(reader, DevicesConfig.class);
		logger.finest("Devices configured: " + config.devices.length);

		for(DeviceConfig device: config.devices) {
			logger.finest("Configuring device: " + device.name);
			StringTokenizer t = new StringTokenizer(device.state);
			String adapter = t.nextToken();
			logger.finest("Adapter: " + adapter );
			String groupAddress = t.nextToken();
			logger.finest("Group address: " + groupAddress);

			// Add group address to list to monitor.
			groupAddresses.add(groupAddress);
		}
		
	}
	
	/**
	 * Start the adapter operation
	 */
	public void start() {
		// start cache
		logger.fine("Creating cache");
		CacheManager cacheMgr = CacheManager.create();
		cacheMgr.addCache(CACHE_NAME);
		cache = cacheMgr.getCache(CACHE_NAME);

		// connect to KNX, monitor connection
		// updates are added in the cache (passed as reference)
		monitor = new KNXMonitor(this);
		monitor.start();
		
		
		
		// continuously update cache
		
	}

	/**
	 * add KNX group address updates to cache
	 * 
	 * @param groupAddress a String object representing the group address 
	 * @param value the String value of the group addresss
	 * @param dpt the DPT object, describing the type of the group address
	 */
	public void deviceUpdate(String groupAddress, String value, DPT dpt) {
		logger.fine("Updating cache for " + groupAddress);
		Element valueElement = new Element(groupAddress, dpt);
		cache.put(valueElement);		
		Element typeElement = new Element(groupAddress + "_dpt", dpt);
		cache.put(typeElement);
		
	}
	
}
