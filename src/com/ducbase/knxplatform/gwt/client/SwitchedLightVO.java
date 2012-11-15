package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;



public class SwitchedLightVO extends JavaScriptObject {
	
	// Overlay types always have protected, zero argument constructors.
	  protected SwitchedLightVO() {}
	  
	// JSNI methods to get stock data.
	  public final native boolean isOn() /*-{ return this.on == "true"; }-*/; 	
	  

}
