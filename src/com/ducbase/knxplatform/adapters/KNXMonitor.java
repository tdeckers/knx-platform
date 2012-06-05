package com.ducbase.knxplatform.adapters;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat;
import tuwien.auto.calimero.dptxlator.DPTXlator2ByteUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
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

public class KNXMonitor extends Thread {
	
	Logger logger = Logger.getLogger(KNXMonitor.class.getName());
	
	private KNXAdapter adapter;

	public KNXMonitor(KNXAdapter adapter) {
		this.adapter = adapter;
		try {
			Connection con = new Connection();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KNXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		
	}
	
	
	class Connection {

			
			Connection() throws UnknownHostException, KNXException  {
				KNXNetworkLink link = new KNXNetworkLinkIP(
						KNXNetworkLinkIP.TUNNEL, 
						new InetSocketAddress(InetAddress.getByName("192.168.2.102"), 0), 
						new InetSocketAddress(InetAddress.getByName("192.168.2.150"), KNXnetIPConnection.IP_PORT), 
						false, 
						new TPSettings(false));
				ProcessCommunicator pc = new ProcessCommunicatorImpl(link);
				link.addLinkListener(new NetworkLinkListener() {
					
					public void linkClosed(CloseEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					public void indication(FrameEvent e) {
						CEMILData data = (CEMILData) e.getFrame();					
						byte[] tpdu = data.getPayload();
						logger.fine("Data: " + data + ", " + tpdu.length + " byte(s)");
												
						
						if (data.getDestination().toString().startsWith("2/1/") || data.getDestination().toString().startsWith("2/6/")) {
							DPTXlator2ByteFloat xlate = null;
							try {
								xlate = new DPTXlator2ByteFloat(DPTXlator2ByteFloat.DPT_TEMPERATURE);
							} catch (KNXFormatException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							xlate.setData(tpdu, 2);
							adapter.deviceUpdate(data.getDestination().toString(), xlate.getValue(), DPTXlator2ByteFloat.DPT_TEMPERATURE);
							logger.fine("TEMP: " + xlate.getValueFloat() + " " + DPTXlator2ByteFloat.DPT_TEMPERATURE.getUnit());
						  
						}
						
						if(data.getDestination().toString().startsWith("2/3/")) {
							DPTXlator8BitUnsigned xlate = null;		
							try {
								xlate = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_SCALING);
							} catch (KNXFormatException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							xlate.setData(tpdu, 2);
							xlate.setAppendUnit(false);
							adapter.deviceUpdate(data.getDestination().toString(), xlate.getValue(), DPTXlator8BitUnsigned.DPT_SCALING);
							logger.fine("PCT: " + xlate.getValue() + " " + DPTXlator8BitUnsigned.DPT_SCALING.getUnit());
						}
						
						DPTXlatorBoolean xlate = null;
						try {
							xlate = new DPTXlatorBoolean(DPTXlatorBoolean.DPT_SWITCH);
						} catch (KNXFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						xlate.setData(tpdu, 1);
						
						logger.fine(data.getSource() + " -- " 
								+ xlate.getValue() + " --> " 
								+ data.getDestination());
						

						
					}
					
					public void confirmation(FrameEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			
		}
		
	
}
