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
	public String gmode;
	
	public String toString() {
		return name + "[" + type + "]";
	}

}
