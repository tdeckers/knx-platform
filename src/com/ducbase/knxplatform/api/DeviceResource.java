package com.ducbase.knxplatform.api;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.ducbase.knxplatform.devices.Device;
import com.ducbase.knxplatform.devices.DeviceManager;

public class DeviceResource {
	
	Logger logger = Logger.getLogger(DeviceResource.class.getName());
	
	  @Context UriInfo uriInfo;
	  @Context Request request;
	  String id;
	  
	  DeviceManager manager = DeviceManager.getInstance();
	  
	  public DeviceResource(UriInfo uriInfo, Request request, String id) {
	    this.uriInfo = uriInfo;
	    this.request = request;
	    this.id = id;
	  }

	  // For the browser
	  @GET
	  @Produces(MediaType.TEXT_XML)
	  public Device getDeviceForBrowser() {
		  int intId = Integer.parseInt(id);
		  Device device = manager.getDevice(intId);
		  if ( device == null )
			  throw new RuntimeException("Get: Device with " + id +  " not found");
		  logger.fine(" ------------ " + device.getClass().getName());
		  return device;
	  }
	  
	  //Application integration     
	  @GET
	  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	  public Device getDevice() {
		  int intId = Integer.parseInt(id);
		  Device device = manager.getDevice(intId);
		  if ( device == null )
			  throw new RuntimeException("Get: Device with " + id +  " not found");
		  return device;
	  }	  
	  
	  // TODO implement PUT / DELETE
	  
}
