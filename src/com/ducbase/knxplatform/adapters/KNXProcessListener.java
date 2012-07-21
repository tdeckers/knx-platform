package com.ducbase.knxplatform.adapters;

import java.util.logging.Logger;

import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat;
import tuwien.auto.calimero.dptxlator.DPTXlator3BitControlled.DPT3BitControlled;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

public class KNXProcessListener implements ProcessListener {
	
	private static Logger logger = Logger.getLogger(KNXProcessListener.class.getName());
	
	private KNXAdapter adapter;

	public KNXProcessListener(KNXAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void groupWrite(ProcessEvent e) {
		String dst = e.getDestination().toString();
		String src = e.getSourceAddr().toString();
		byte[] asdu = e.getASDU();
		logger.fine(src + " -> " + dst + ": " + asdu.length);
		if (dst.startsWith("1/1/")) {
			this.writeBoolean(dst, asdu);
		}
		if(dst.startsWith("2/3/")) {
			this.writePercentage(dst, asdu);
		}
		if (dst.startsWith("2/1/") || dst.startsWith("2/6/")) {
			this.writeTemperature(dst, asdu);
		}
	}



	@Override
	public void detached(DetachEvent e) {
		
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
		adapter.deviceUpdate(dst, xlate.getValue(), DPTXlatorBoolean.DPT_SWITCH);
		logger.fine("SWITCH: " + xlate.getValue() + " " + DPTXlatorBoolean.DPT_SWITCH.getUnit());
		
	}
	
}
