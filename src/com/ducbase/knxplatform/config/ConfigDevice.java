package com.ducbase.knxplatform.config;

import javax.xml.bind.annotation.XmlElement;

public class ConfigDevice {
	
	public String type;	
	public String name;
	public String description;
	public String statusGroup;
	public String switchGroup;
	
	public String toString() {
		return name + "[" + type + "]";
	}

}
