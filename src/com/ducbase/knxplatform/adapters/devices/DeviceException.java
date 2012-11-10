package com.ducbase.knxplatform.adapters.devices;

public class DeviceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DeviceException(String message) {
		super(message);
	}
	
	public DeviceException(String message, Throwable cause) {
		super(message, cause);
	}

}
