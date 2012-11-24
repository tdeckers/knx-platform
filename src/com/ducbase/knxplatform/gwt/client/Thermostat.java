package com.ducbase.knxplatform.gwt.client;

import com.ducbase.knxplatform.gwt.client.TemperatureSensor.TemperatureSensorVO;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Thermostat extends Device implements ClickHandler {
	
	private Image modeImage = new Image();
	private String imageUnknown = "img/unknown.png";
	private String imageAuto = "img/hvac_auto.png";
	private String imageComfort = "img/hvac_home.png";	
	private String imageStandby = "img/hvac_standby.png";
	private String imageSleep = "img/hvac_sleep.png";
	private String imageFrost = "img/hvac_frost.png";
	
	private Label actualLabel = new Label();
	private Label setpointLabel = new Label();
	
	private float actualTemp = 0;
	private float setpointTemp = 0;
	private int mode = -1;
	private int variable = -1;
	
	public Thermostat() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this.setMode(-1);
		modeImage.setSize("30px", "30px");
		modeImage.addClickHandler(this);
		panel.add(modeImage);
		
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		
		setpointLabel.setStyleName("knx-thermostat-setpoint");
		vPanel.add(setpointLabel);
		actualLabel.setStyleName("knx-thermostat-actual");
		vPanel.add(actualLabel);
		setpointLabel.addClickHandler(this);
		panel.add(vPanel);
		
		this.setActualTemp(0);
		this.setSetpointTemp(0);
		
		this.initWidget(panel);
		
		setStyleName("knx-thermostat");
		
	}
	
	public void setActualTemp(float temp) {
		this.actualTemp = temp;
		actualLabel.setText(this.actualTemp + " *C");
	}
	
	public void setSetpointTemp(float temp) {
		this.setpointTemp = temp;
		setpointLabel.setText(this.setpointTemp + " *C");
	}
	
	public void setMode(int mode) {
		this.mode = mode;
		switch(mode) {
		case 0:
			modeImage.setUrl(imageAuto);
			break;
		case 1:
			modeImage.setUrl(imageComfort);
			break;
		case 2:
			modeImage.setUrl(imageStandby);
			break;
		case 3:
			modeImage.setUrl(imageSleep);
			break;
		case 4:
			modeImage.setUrl(imageFrost);
			break;
		default:
			modeImage.setUrl(imageUnknown);
		}
	}
	
	public void setVariable(int variable) {
		this.variable = variable;
		
		if (this.variable > 0) {
			actualLabel.setStyleDependentName("heating", true);
		} else {
			actualLabel.setStyleDependentName("heating", false);
		}
	}
	
	public void setSize(String width, String height) {
		modeImage.setSize(width, height);
	}
	
	@Override
	public void update(String json) {
		ThermostatVO thermo = JsonUtils.safeEval(json);
		this.setMode(thermo.getMode());
		this.setSetpointTemp(thermo.getSetpoint());
		this.setActualTemp(thermo.getActual());
		this.setVariable(thermo.getVariable());
	}
	
	static public class ThermostatVO extends JavaScriptObject {
		// Overlay types always have protected, zero argument constructors.
		  protected ThermostatVO() {}
		  
		// JSNI methods to get stock data.
		  public final native int getMode() /*-{ return parseInt(this.mode); }-*/;
		  public final native float getSetpoint() /*-{ return parseFloat(this.setpoint); }-*/;
		  public final native float getActual() /*-{ return parseFloat(this.temperature); }-*/;
		  public final native int getVariable() /*-{ return parseInt(this.variable); }-*/;
	}
	

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == modeImage) {
			ModePopup popup = new ModePopup();
			popup.showRelativeTo(Thermostat.this);			
		}
	}
	
	public class ModePopup extends PopupPanel implements ClickHandler {
		
		Image comfortImage = new Image(imageComfort);
		Image standbyImage = new Image(imageStandby);
		Image sleepImage = new Image(imageSleep);
		Image frostImage = new Image(imageFrost);
		
		public ModePopup() {			
			super(true);
			HorizontalPanel panel = new HorizontalPanel();
			comfortImage.setSize("30px", "30px");
			comfortImage.addClickHandler(this);
			panel.add(comfortImage);
			standbyImage.setSize("30px", "30px");
			standbyImage.addClickHandler(this);
			panel.add(standbyImage);
			sleepImage.setSize("30px", "30px");
			sleepImage.addClickHandler(this);
			panel.add(sleepImage);
			frostImage.setSize("30px", "30px");
			frostImage.addClickHandler(this);
			panel.add(frostImage);			
			
			setWidget(panel);		
			
			setStyleName("knx-thermostat-popup");
		}

		@Override
		public void onClick(ClickEvent event) {
			
			if (event.getSource() == comfortImage) {
				processClick(1);
			}
			if (event.getSource() == standbyImage) {
				processClick(2);
			}
			if (event.getSource() == sleepImage) {
				processClick(3);
			}
			if (event.getSource() == frostImage) {
				processClick(4);
			}
		}
		
		void processClick(int mode) {
			Thermostat.this.setMode(mode);
			ModePopup.this.hide();
			
			// Send update to server!
			String data = "{'mode': '" + mode + "'}";
			ServiceClient.sendData(data,Thermostat.this.getUrl());
		}
	}

}
