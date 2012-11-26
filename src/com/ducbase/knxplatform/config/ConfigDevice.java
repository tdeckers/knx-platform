package com.ducbase.knxplatform.config;

import javax.xml.bind.annotation.XmlElement;

public class ConfigDevice {
	
	public String type;	
	public String name;
	public String id;
	public String description;
	public String gstatus;
	public String gswitch;
	public String gvariable;
	public String gactual;
	public String gsetpointr;
	public String gsetpointw;
	public String gmoder;
	public String gmodew;
	public String gdimr;
	public String gdimw;
	public String gupdown;
	public String gstop;
	public String gposr;
	public String gposw;
	
	public String toString() {
		return name + "[" + type + "]";
	}

}
