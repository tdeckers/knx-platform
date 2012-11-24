package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.ui.Image;

public class SwitchedLight extends Device implements ClickHandler {

	private Image image = new Image();
	private String imageOn = "img/light_bulb_on.png";
	private String imageOff = "img/light_bulb_off.png";
	
	private boolean on;
	
	public SwitchedLight() {
		image.setUrl(imageOff);
		image.addClickHandler(this);
		
		initWidget(image);
		
		setStyleName("knx-switchedlight");
	}
	
	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
		if (on == true) {
			image.setUrl(imageOn);
		} else {
			image.setUrl(imageOff);
		}
	}

	public void setSize(String width, String height) {
		image.setSize(width, height);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if (on) {
			this.setOn(false);
		} else {
			this.setOn(true);			
		}
		
		// Send update!
		 String data = "{'on': '" + this.isOn() + "'}";
		 ServiceClient.sendData(data,this.getUrl());
	}
	
	public void addClickHandler(ClickHandler handler) {
		image.addClickHandler(handler);
	}
	
	public void addDoubleClickHandler(DoubleClickHandler handler) {
		image.addDoubleClickHandler(handler);
	}

	public void addTouchMoveHandler(TouchMoveHandler handler) {
		image.addTouchMoveHandler(handler);
	}

	@Override
	public void update(String json) {
		SwitchedLightVO light = JsonUtils.safeEval(json);
		this.setOn(light.isOn());
	}
	
	static public class SwitchedLightVO extends JavaScriptObject {
		
		// Overlay types always have protected, zero argument constructors.
		  protected SwitchedLightVO() {}
		  
		// JSNI methods to get stock data.
		  public final native boolean isOn() /*-{ return this.on == "true"; }-*/; 	
	  
	}

}
