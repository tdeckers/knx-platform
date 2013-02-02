package com.ducbase.knxplatform.adapters;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.xml.bind.JAXBException;

import org.jivesoftware.smack.XMPPException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.management.ManagementService;
import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.datapoint.StateDP;
import tuwien.auto.calimero.dptxlator.DPT;
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

import com.ducbase.knxplatform.WebSocketManager;
import com.ducbase.knxplatform.adapters.devices.KNXDimmedLight;
import com.ducbase.knxplatform.adapters.devices.KNXShutter;
import com.ducbase.knxplatform.adapters.devices.KNXSwitched;
import com.ducbase.knxplatform.adapters.devices.KNXTemperatureSensor;
import com.ducbase.knxplatform.adapters.devices.KNXThermostat;
import com.ducbase.knxplatform.connectors.GoogleTalkConnector;
import com.ducbase.knxplatform.devices.Device;
import com.ducbase.knxplatform.devices.DeviceManager;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONMarshaller;

/**
 * abstraction layer for the KNX interface.
 * 
 * This class will:
 * <li>keep a map of all KNX devices to manage/monitor</li>
 * <li>keep a map of group addresses and the device properties they map to</li>
 * 
 * 
 * @author tom@ducbase.com
 *
 */
public class KNXAdapter {

	static Logger logger = Logger.getLogger(KNXAdapter.class.getName());
	
	private KNXNetworkLink link;
	private ProcessCommunicator pc;
	private KNXProcessListener listener;
	
	private static final String CACHE_NAME = "distributed-knx-cache";  // correspond with name in ehcache.xml.
	
	Map<String, String> listenFor = new HashMap<String, String>();  // datapoint - device id
	Map<String, DPT> typeMap = new HashMap<String, DPT>();
	
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
		// Adding listener
		listener = new KNXProcessListener(this);
		
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
	public void prefetch() {	
		
		Runnable task = new Runnable() {
			
			final int MAX_RETRIES = 5;
			
			boolean hasRun = false;
			int retries = 0;

			@Override
			public void run() {
				while (!hasRun) {
					
					if (retries > MAX_RETRIES) {
						logger.warning("Retries exceeded. Not retrying to prefetch.");
						return;
					} else {
						retries++;
					}
					
					try {
						Thread.sleep(20 * 1000); // wait 20 secs before starting to give us time to boot
					} catch (InterruptedException e1) {}
					
					if (pc == null) {
						logger.warning("Can't prefetch.  Not (yet?) connected!");
						continue;
					}
					
					logger.info("Prefetching states from KNX (in a separate thread)");
					
					StringBuffer prefetchList = new StringBuffer();
					for(String address: listenFor.keySet()) {
						prefetchList.append(address + " ");
						try {
							GroupAddress ga = new GroupAddress(address);
							DPT dpt = typeMap.get(address);
							Datapoint dp = new StateDP(ga, "", 0, dpt.getID());
							pc.read(dp);
						} catch (KNXException e) {
							logger.warning("Exception while prefetching " + address + " - " + e.getMessage());
						}
						try {
							Thread.sleep(250); // wait a bit before starting the next... don't overload KNX.
						} catch (InterruptedException e1) {}
					}
					
					logger.info("Done prefetching.");
					logger.fine("Prefetch list: " + prefetchList);
					
					hasRun = true;					
				}

			}
			
		};
		Thread thread = new Thread(task);
		thread.start();		
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
		logger.fine("Connecting to KNX");
	
		// find own IP address
		InetAddress localAddress = null;
		try {
			String hostName = InetAddress.getLocalHost().getHostName();
			InetAddress addrs[] = InetAddress.getAllByName(hostName);	
			if (addrs != null && logger.isLoggable(Level.FINE)) {  // only run this if logging fine.
				String msg = "Hostname: " + hostName + ", got " + addrs.length + " address(es)";
				for (InetAddress a: addrs) {
					msg += " - " + a.getHostAddress();
				}
				logger.fine(msg);
			}
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
		if (link != null) { // cleanup in case we've been here before.
			link.close();			
		}
		
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
						try {Thread.sleep(5 * 1000); } catch (Exception ex) {}; // wait 5 seconds to avoid frantic reconnect.
						connect();
						return;
					}
					if (!link.isOpen()) {
						logger.severe("KNX Link lost!");
					}
					logger.severe("Through the cracks: " + e.getSource());
				}
				
				@Override
				public void indication(FrameEvent e) {
					// logger.fine("INDICATION: " + e.toString());				
				}
				
				@Override
				public void confirmation(FrameEvent e) {
					// logger.fine("CONFIRMATION: " + e.toString());
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
		logger.info("Disconnecting from KNX");
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
	@Deprecated
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
	
	public void updateDevice(String groupAddress, String value) {
		logger.fine("Updating cache for " + groupAddress);
		Element valueElement = new Element(groupAddress, value);
		cache.put(valueElement);
		
		// find device from group ID (look in listenFor)
		String deviceId = listenFor.get(groupAddress);
		DeviceManager deviceManager = DeviceManager.getInstance();
		Device device = deviceManager.getDevice(deviceId);
		
		// TODO - this should change in Events and subscribing to Events!
		// B2 - Woning volledig
		// B3 - Woning gedeeltelijk
		// 0/1/3 - Brand
		// 0/1/4 - Alarm
		try {
			if (device.getId().equals("B2")) {
				boolean on = ((KNXSwitched) device).isOn();
				GoogleTalkConnector.getInstance().sendMessage("Alarm " + (on ? "on":"off"));
			}
// Let's not send chat messages for partial alarm... annoying!
//			if (device.getId().equals("B3")) {
//				boolean on = ((KNXSwitched) device).isOn();	
//				GoogleTalkConnector.getInstance().sendMessage("Alarm (partial) " + (on ? "on":"off"));
//			}
		} catch (XMPPException | IOException e) {
			logger.severe("Unable to send message: " + e.getMessage());
		}					
		// END TODO
		
		// device -> JSON
		Class[] classes = new Class[] {KNXSwitched.class, KNXDimmedLight.class, KNXTemperatureSensor.class, KNXThermostat.class, KNXShutter.class};
		try {
			JSONJAXBContext context = new JSONJAXBContext(classes);	
			JSONMarshaller m = context.createJSONMarshaller();
			StringWriter writer = new StringWriter();
			m.marshallToJSON(device, writer);
			String json = writer.toString();
			logger.fine("JSON: " + json);

			WebSocketManager wsMgr = WebSocketManager.getInstance();
			wsMgr.broadcast(json);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			logger.fine("About to send...");
			pc.write(address, value, ProcessCommunicator.UNSCALED);
			logger.fine("Sent.");
		} catch (KNXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	public synchronized void sendIntScaled(String groupAddress, int value) {
		try {
			GroupAddress address = new GroupAddress(groupAddress);
			logger.fine("About to send...");
			pc.write(address, value, ProcessCommunicator.SCALING);
			logger.fine("Sent.");
		} catch (KNXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public synchronized void sendFloat(String groupAddress, float value) {
		try {
			GroupAddress address = new GroupAddress(groupAddress);
			logger.fine("About to send...");
			pc.write(address, value);
			logger.fine("Sent.");
		} catch (KNXException e) {
			// TODO Auto-generated catch block
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

	/**
	 * Register datapoints to listen for on the KNX bus, for device with id.
	 * 
	 * @param listenGroups
	 * @param id
	 */
	public void registerListenFor(String[] listenGroups, String id) {
		for(String dp: listenGroups) {
			this.listenFor.put(dp, id);
		}
	}

	public void addTypeMap(Map<String, DPT> typeMap) {
		this.typeMap.putAll(typeMap);		
	}


}
