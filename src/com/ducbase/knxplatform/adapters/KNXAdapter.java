package com.ducbase.knxplatform.adapters;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.management.MBeanServer;

import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat;
import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.event.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.management.ManagementService;

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
	
	private KNXNetworkLink link;
	private ProcessCommunicator pc;
	private KNXProcessListener listener = new KNXProcessListener();
	
	private static final String CACHE_NAME = "distributed-knx-cache";  // correspond with name in ehcache.xml.
	List<String> booleanGroupAddresses = new ArrayList<String>();
	
	private Date lastConnected;	
	
	/**
	 * a KNXAdapter maintains a cache of group address states.  Mainly for those group addresses used for Device state's.
	 */
	private Cache cache;
	
	private static KNXAdapter instance;
	
	/**
	 * Create a KNXAdapter
	 * 
	 * TODO: make singleton
	 */
	private KNXAdapter() {
		logger.info("Creating KNX Adapter");
		
		// start cache
		logger.fine("Creating cache");
		CacheManager cacheMgr = CacheManager.create();
		
		// Register cache manager as mBean!
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		ManagementService.registerMBeans(cacheMgr, mBeanServer, false, false, false, true);
		
		cache = cacheMgr.getCache(CACHE_NAME);
		
	}
	
	public static KNXAdapter getInstance() {
		if (instance == null) {
			synchronized(KNXAdapter.class) {
				if (instance == null) {
					instance = new KNXAdapter();
				}
			}
		}
		return instance;
	}
	
	
	/**
	 * prefetch state from KNX.  This method will send out read requests for all known devices.  
	 * The responses will preload the cache.
	 * 
	 * TODO This is a hacked up version.  This should load from a list of configured devices.
	 * 
	 */
	private void prefetch() {
		logger.info("Prefetching states from KNX");
		String[] boolGroups = {"0/1/0", "0/1/1", "0/1/3", "0/1/4", // Alarm status.
				"2/0/4"};	// heating on/off
						
		for(String address: boolGroups) {
			try {
				pc.readBool(new GroupAddress(address));
			} catch (KNXException e) {
				e.printStackTrace();
			}
		}
		
		String scalingGroupsPrefix = "2/3/";  // just heating variables for now.
		for(int i = 0; i <= 13; i++) {
			if (i == 1 || i == 2 || i == 3 || i == 11) continue; // nothing at 2/3/x 			
			try {
				pc.readUnsigned(new GroupAddress(scalingGroupsPrefix + i), ProcessCommunicator.SCALING);
			} catch (KNXException e) {
				e.printStackTrace();
			}
		}
		
		String[] floatGroupPrefixes = {"2/1/", //actual temp 
								"2/2/"}; // setpoint temp
		for(String prefix: floatGroupPrefixes) {
			for(int i = 0; i <= 9; i++) { // for both prefixes, get all groups
				try {
					pc.readFloat(new GroupAddress(prefix + i));
				} catch (KNXException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		logger.info("Done prefetching states from KNX");		
		
	}

	/**
	 * check adapter connection to KNX.  More specifically, it checks the <code>KNXNetworkLinkIP</code> health.
	 * If not connected, this method does some cleanup too.
	 * 
	 * @return a <code>boolean</code> to indicate adapter health.
	 */
	public boolean isOk() {
		if (link == null) {
			logger.warning("KNX Link is null");
			if (pc != null) {
				pc.detach();
			}
			return false;
		}
		if (!link.isOpen()) {
			logger.warning("KNX Link not open");
			if (pc != null) {
				pc.detach();
			}
			link.close();
			return false;
		}

		logger.fine("Link is OK!");
		return true;		
	}
	
	public String getLastConnect() {
		if (this.lastConnected == null) {
			return "";
		}
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
		String retVal = formatter.format(this.lastConnected);
		return retVal;
	}
	
	public synchronized void connect() {
		logger.fine("Connecting KNX");
	
		// find own IP address
		InetAddress localAddress = null;
		try {
			String hostName = InetAddress.getLocalHost().getHostName();
			InetAddress addrs[] = InetAddress.getAllByName(hostName);			
			for(InetAddress addr: addrs) {
				// TODO: hardcoding to my subnet, change to more flexible mechanism later.
				//if( !addr.isLoopbackAddress() && addr.isSiteLocalAddress() ) {
				if (addr.getHostAddress().startsWith("192.168.2.")) {
					logger.fine("IP Address found: " + addr.getHostAddress());
					localAddress = addr;
				}
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		logger.info("Using ip address: " + localAddress.getHostAddress());
			
		// connect to KNX
		try {
			link = new KNXNetworkLinkIP(
					KNXNetworkLinkIP.TUNNEL, 					
					new InetSocketAddress(localAddress, 0), 
					new InetSocketAddress(InetAddress.getByName("192.168.2.150"), KNXnetIPConnection.IP_PORT), 
					false, // don't use NAT
					TPSettings.TP1);  // select TP1
			link.addLinkListener(new NetworkLinkListener() {				
				@Override
				public void linkClosed(CloseEvent e) {
					if (!e.isUserRequest()) {
						logger.warning("Link closed! Let's reconnect. (" + e.getReason() + " | " + e.getSource().toString() + ")");
						connect();
					}
					if (!link.isOpen()) {
						logger.severe("KNX Link lost!");
					}
					logger.warning("Through the cracks: " + e.getSource());
				}
				
				@Override
				public void indication(FrameEvent e) {
					logger.fine("INDICATION: " + e.toString());				
				}
				
				@Override
				public void confirmation(FrameEvent e) {
					logger.fine("CONFIRMATION: " + e.toString());
				}
			});
			
			if (pc != null) {
				pc.removeProcessListener(listener);
				pc.detach();
			}
			
			pc = new ProcessCommunicatorImpl(link);
			pc.setResponseTimeout(2);  // TODO - make configurable?
			if (listener != null) {
				pc.addProcessListener(listener);
			}


		} catch (KNXException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		// set state
		this.lastConnected = new Date();
	}
	

	public void disconnect() {
		if (pc != null) {
			pc.removeProcessListener(listener);
			pc.detach();
		}
		if (link != null) {
			link.close();
			link = null;
		}		
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
		Element valueElement = new Element(groupAddress, value);
		cache.put(valueElement);		
		//TODO: no need to save DPT... we'll use device definition for that.
		// Use tuwien.auto.calimero.dptxlator.TranslatorTypes is needed
		// btw: is dpt.getID() enough? Does it include the mainType of the DPT?
		Element typeElement = new Element(groupAddress + "_dpt", dpt.getID());
		cache.put(typeElement);		

	}
	
	public synchronized void sendBoolean(String groupAddress, boolean value) {
		try {
			GroupAddress address = new GroupAddress(groupAddress);
			pc.write(address, value);
		} catch (KNXLinkClosedException | KNXFormatException | KNXTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void sendIntUnscaled(String groupAddress, int value) {
		try {
			GroupAddress address = new GroupAddress(groupAddress);			
			pc.write(address, value, ProcessCommunicator.UNSCALED);
		} catch (KNXException e) {
			// TODO Auto-generated catch block
//			Oct 06, 2012 7:25:57 PM org.apache.catalina.realm.LockOutRealm authenticate
//			WARNING: An attempt was made to authenticate the locked user "admin"
//			tuwien.auto.calimero.link.KNXLinkClosedException: link closed
//			        at tuwien.auto.calimero.link.KNXNetworkLinkIP.doSend(KNXNetworkLinkIP.java)
//			        at tuwien.auto.calimero.link.KNXNetworkLinkIP.send(KNXNetworkLinkIP.java)
//			        at tuwien.auto.calimero.link.KNXNetworkLinkIP.sendRequestWait(KNXNetworkLinkIP.java)
//			        at tuwien.auto.calimero.process.ProcessCommunicatorImpl.write(ProcessCommunicatorImpl.java)
//			        at tuwien.auto.calimero.process.ProcessCommunicatorImpl.write(ProcessCommunicatorImpl.java)
//			        at com.ducbase.knxplatform.adapters.KNXAdapter.sendIntUnscaled(KNXAdapter.java:197)
//			        at com.ducbase.knxplatform.adapters.KNXService.send(KNXService.java:97)			
			
			e.printStackTrace();
		}
	}	
	
	/** 
	 * TODO remove this method... temporary one.
	 * @param groupAddress
	 * @return
	 */
	public String getValueForGroupAddress(String groupAddress) {
		Element valueEl = cache.get(groupAddress);
		if (valueEl == null) return "";
		return (String) valueEl.getValue();		
	}

	public void registerBooleanGroup(String groupAddress) {
		this.booleanGroupAddresses.add(groupAddress);
	}

	
}
