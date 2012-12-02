package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.user.client.ui.Label;

public class BooleanStatus extends Device {

	Label label = new Label();
	private boolean on;
	
	public BooleanStatus() {
		label.setStyleName("knx-booleanstatus-label");		
		initWidget(label);
		
		setStyleName("knx-booleanstatus");
	}	
	
	public boolean isOn() {
		return on;
	}
	
	private void setOn(boolean on) {
		this.on = on;
		if (on == true) {
			label.setText("ON");
		} else {
			label.setText("OFF");
		}
	}	
	
	@Override
	public void update(String json) {
		BooleanStatusVO status = JsonUtils.safeEval(json);
		this.setOn(status.isOn());
	}
	
	static public class BooleanStatusVO extends JavaScriptObject {
		
		// Overlay types always have protected, zero argument constructors.
		  protected BooleanStatusVO() {}
		  
		// JSNI methods to get stock data.
		  public final native boolean isOn() /*-{ return this.on == "true"; }-*/;
	  
	}	

}
