package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class TemperatureSensor extends Device {
	
	private Label label = new Label();
	
	public TemperatureSensor() {
		this.setValue(0);
		initWidget(label);
		
		setStyleName("knx-tempsensor");
	}

	public void setValue(float value) {
		label.setText(value + " *C");
	}

	@Override
	public void update(String json) {
		TemperatureSensorVO sensor = JsonUtils.safeEval(json);
		this.setValue(sensor.getTemperature());	
	}
	
	static public class TemperatureSensorVO extends JavaScriptObject {
		
		// Overlay types always have protected, zero argument constructors.
		  protected TemperatureSensorVO() {}
		  
		// JSNI methods to get stock data.
		  public final native float getTemperature() /*-{ return parseFloat(this.temperature); }-*/; 	
	}
	

}
