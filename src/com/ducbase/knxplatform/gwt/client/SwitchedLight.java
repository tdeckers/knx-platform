package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;

public class SwitchedLight extends Composite implements ClickHandler {

	private Image image = new Image();
	
	private boolean on;
	
	public SwitchedLight() {
		image.setUrl("img/light_bulb_off.png");
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
			image.setUrl("img/light_bulb_on.png");
		} else {
			image.setUrl("img/light_bulb_off.png");
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
	}
	
	public void addClickHandler(ClickHandler handler) {
		image.addClickHandler(handler);
	}
	
}
