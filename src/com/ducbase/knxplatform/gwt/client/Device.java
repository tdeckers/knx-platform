package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

public abstract class Device extends Composite {
	
	private String id;
	private static String urlBase;
	
//	static {
//		String[] pathArray = Window.Location.getPath().split("/");
//		StringBuffer tempUrl = new StringBuffer();
//		for ( int i = 0; i < pathArray.length -1 ; i++ ) { // don't include the last part (which is index.html)
//			tempUrl.append(pathArray[i]);
//			tempUrl.append('/');
//		}		
//		tempUrl.append("rest/devices/");
//		Device.urlBase = tempUrl.toString();
//	}
	
	public static void setBaseUrl(String param) {
		Device.urlBase = param + "rest/devices/";
	}
	
	public Device() {
		
	}

	abstract public void update(String json);

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		if (id != null) {
			return urlBase + id;
		} else {
			return null;
		}
	}
	

}
