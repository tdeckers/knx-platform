package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;

public class SwitchedLight extends Device implements ClickHandler {

	private Image image = new Image();
	private String imageOn = "img/light_bulb_on.png"; // default value
	private String imageOff = "img/light_bulb_off.png"; // default value
	
	private boolean on;
	HandlerRegistration clickHandler;
	
	public SwitchedLight() {
		image.setUrl(imageOff);
		clickHandler = image.addClickHandler(this);
		
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
		this.processClick();
	}
	
	public void processClick() { // method is reusable outside class.
		if (on) {
			this.setOn(false);
		} else {
			this.setOn(true);			
		}
		
		// Send update!
		 String data = "{'on': '" + this.isOn() + "'}";
		 ServiceClient.sendData(data,this.getUrl());			
	}
	
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return image.addClickHandler(handler);
	}
	
	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
		return image.addDoubleClickHandler(handler);
	}

	public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
		return image.addTouchMoveHandler(handler);
	}
	
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return image.addTouchStartHandler(handler);
	}
	
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return image.addTouchEndHandler(handler);
	}
	
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return image.addMouseDownHandler(handler);
	}
	
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return image.addMouseMoveHandler(handler);
	}
	
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return image.addMouseUpHandler(handler);
	}
	
	
	public String getImageOn() {
		return imageOn;
	}

	public void setImageOn(String imageOn) {
		this.imageOn = imageOn;
	}

	public String getImageOff() {
		return imageOff;
	}

	public void setImageOff(String imageOff) {
		this.imageOff = imageOff;
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
