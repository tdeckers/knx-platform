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
	
	static final String CACHE_NAME = "knx-cache";
	List<String> groupAddresses = new ArrayList<String>();
	
	//public boolean started = false;
	public enum State { STARTED, STOPPED, SPUTTER }
	public State state = State.STOPPED;
	
	
	
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

	}
	
	/**
	 * Start the adapter operation.  This will connect the adapter to the KNX network.
	 */
	public void start() {
		logger.info("Starting KNX Adapter");
		// start cache
		logger.fine("Creating cache");
		CacheManager cacheMgr = CacheManager.create();
		
		cache = new Cache(
				new CacheConfiguration(CACHE_NAME, 1000)
					.overflowToDisk(false)
					.diskPersistent(false));
		
		
		cacheMgr.addCache(cache);
		
		logger.fine("Connecting KNX");
		this.connect();

		// set state
		state = State.STARTED;		
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
			state = State.SPUTTER;
			return false;
		}
		if (!link.isOpen()) {
			logger.warning("KNX Link not open");
			if (pc != null) {
				pc.detach();
			}
			link.close();
			state = state.SPUTTER;
			return false;
		}

// Let's first see whether we can catch these stalls by just checking the link.		
//		try {
//			pc.readBool(new GroupAddress("1/4/0"));
//		} catch (KNXException e) {
//			e.printStackTrace();
//			return false;
//		}
		logger.fine("Link is OK!");
		return true;		
	}
	
	public void connect() {
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
		}
		logger.info("Using ip address: " + localAddress.getHostAddress());
			
		// connect to KNX
		try {
			link = new KNXNetworkLinkIP(
					KNXNetworkLinkIP.TUNNEL, 					
					new InetSocketAddress(localAddress, 0), 
					new InetSocketAddress(InetAddress.getByName("192.168.2.150"), KNXnetIPConnection.IP_PORT), 
					false, 
					new TPSettings(false));
			pc = new ProcessCommunicatorImpl(link);
			pc.addProcessListener(new KNXProcessListener(this));

		} catch (KNXException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void stop() {
		logger.info("Stopping KNX Adapter");
		pc.detach();
		link.close();
		
		state = State.SPUTTER;
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
		Element typeElement = new Element(groupAddress + "_dpt", dpt);
		cache.put(typeElement);		
	}
	
	public void sendBoolean(String groupAddress, boolean value) {
		if (state != State.STARTED) {
			logger.warning("Can't send: KNX Adapter not started");
			return;
		}
		try {
			GroupAddress address = new GroupAddress(groupAddress);
			pc.write(address, value);
		} catch (KNXLinkClosedException | KNXFormatException | KNXTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendIntUnscaled(String groupAddress, int value) {
		if (state != State.STARTED) {
			logger.warning("Can't send: KNX Adapter not started");
			return;
		}
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
	
}
