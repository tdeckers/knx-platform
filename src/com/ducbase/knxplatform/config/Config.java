package com.ducbase.knxplatform.config;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Config {
	
	public List<ConfigDevice> devices;

}
