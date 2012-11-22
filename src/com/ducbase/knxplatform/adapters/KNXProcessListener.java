package com.ducbase.knxplatform.adapters;

import java.util.logging.Level;
import java.util.logging.Logger;

import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat;
import tuwien.auto.calimero.dptxlator.DPTXlator3BitControlled.DPT3BitControlled;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import tuwien.auto.calimero.dptxlator.TranslatorTypes;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

public class KNXProcessListener implements ProcessListener {
	
	private static Logger logger = Logger.getLogger(KNXProcessListener.class.getName());
	
	private KNXAdapter adapter;

	public KNXProcessListener(KNXAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * receives <code>ProcessEvent</code> from the KNX bus.
	 */
	@Override
	public void groupWrite(ProcessEvent e) {
		String dst = e.getDestination().toString();
		String src = e.getSourceAddr().toString();
		byte[] asdu = e.getASDU();
		
		// Need to validate asdu length!  If length == 0, then a RuntimeException is thrown.
		// This will cause the Listener to stop receiving messages!
		if (asdu.length == 0) {
			logger.fine("Zero length ASDU received, ignoring.");
			return;
		}
		
		// New processing logic!!
		if (adapter.listenFor.containsKey(dst)) {  // should we listen for this group?
			if (!adapter.typeMap.containsKey(dst)) { // do we have a mapping?
				// this can only happen if something went wrong when device got added.
				logger.warning("TypeMap doesn't contain mapping for destination " + dst + ". Ignoring...");
				return;
			}
			logger.fine("Receive from " + src);
			this.receive(dst, asdu); // process the received message
			return; // TODO: can remove this once old processing logic is gone.
		}
		// End new processing logic !!
		
		try {
			logger.fine(src + " -> " + dst + ": " + asdu.length);
			// Alarm status
			if (dst.startsWith("0/1/")) {
				this.writeBoolean(dst, asdu);
			}
			// Lights dimming value
			if (dst.startsWith("1/3/")) {
				this.writePercentage(dst, asdu);
			}
			// Status lights
			if (dst.startsWith("1/4/")) {
				this.writeBoolean(dst, asdu);
			}
			// Heating on/off
			if (dst.equals("2/0/4")) {
				this.writeBoolean(dst, asdu);			
			}
			// Actual temperature (rooms + outside + floor)
			if (dst.startsWith("2/1/") || dst.startsWith("2/6/")) {
				this.writeTemperature(dst, asdu);
			}
			// Setpoint temperature
			if (dst.startsWith("2/2/")) {
				this.writeTemperature(dst, asdu);
			}
			// Heating variable
			if(dst.startsWith("2/3/")) {
				this.writePercentage(dst, asdu);
			}
			// Operating mode
			if (dst.startsWith("2/4/")) {
				// TODO: not implemented
			}
			// Shutter position
			if (dst.startsWith("3/3/")) {
				this.writePercentage(dst, asdu);
			}
		}catch (Exception ex) {
			logger.log(Level.WARNING, "EXCEPTION", ex);
		}

	}

	@Override
	public void detached(DetachEvent e) {
		logger.severe("KNXProcessListener detached from link");
	}
	
	private void writePercentage(String dst, byte[] asdu) {
		DPTXlator8BitUnsigned xlate = null;		
		try {
			xlate = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_SCALING);
		} catch (KNXFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		xlate.setData(asdu);
		xlate.setAppendUnit(false);
		adapter.deviceUpdate(dst, xlate.getValue(), DPTXlator8BitUnsigned.DPT_SCALING);
		logger.fine("PCT: " + xlate.getValue() + " " + DPTXlator8BitUnsigned.DPT_SCALING.getUnit());
	}
	
	private void writeTemperature(String dst, byte[] asdu) {
		DPTXlator2ByteFloat xlate = null;
		try {
			xlate = new DPTXlator2ByteFloat(DPTXlator2ByteFloat.DPT_TEMPERATURE);
		} catch (KNXFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		xlate.setData(asdu);
		xlate.setAppendUnit(false);
		adapter.deviceUpdate(dst, xlate.getValue(), DPTXlator2ByteFloat.DPT_TEMPERATURE);
		logger.fine("TEMP: " + xlate.getValueFloat() + " " + DPTXlator2ByteFloat.DPT_TEMPERATURE.getUnit());
	}
	
	private void writeBoolean(String dst, byte[] asdu) {
		DPTXlatorBoolean xlate = null;
		try {
			xlate = new DPTXlatorBoolean(DPTXlatorBoolean.DPT_SWITCH);
		} catch (KNXFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		xlate.setData(asdu);
		xlate.setAppendUnit(false);
		adapter.deviceUpdate(dst, xlate.getValue(), DPTXlatorBoolean.DPT_SWITCH);
		logger.fine("SWITCH: " + xlate.getValue() + " " + DPTXlatorBoolean.DPT_SWITCH.getUnit());		
	}
	
	private void receive(String dst, byte[] asdu) {
		// Get DPT to figure out which translator to use.
		DPT dpt = adapter.typeMap.get(dst);	
		DPTXlator xlator;
		try {
			xlator = TranslatorTypes.createTranslator(dpt);
		} catch (KNXException e) {
			logger.warning("No translator could be created for type " + dpt.getID());
			return; // stop processing.
		}
		xlator.setData(asdu);
		xlator.setAppendUnit(false);
		// Now update state
		adapter.updateDevice(dst, xlator.getValue());
		logger.fine("UPDATE: " + dst + " <== " + xlator.getValue());
	}
	
}
