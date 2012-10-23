package com.ducbase.knxplatform.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.ducbase.knxplatform.devices.Device;
import com.ducbase.knxplatform.devices.DeviceManager;
import com.sun.istack.internal.logging.Logger;

@Path("/devices")
public class DevicesResource {
	
	Logger logger = Logger.getLogger(DevicesResource.class);

	DeviceManager manager = DeviceManager.getInstance();
	
	  @Context UriInfo uriInfo;
	  @Context Request request;
	
	  // Return the list of devices to the user in the browser
	  @GET
	  @Produces(MediaType.TEXT_XML)
	  public List<Device> getDevicesForBrowser() {	    
	    return manager.getDevices();
	  }
	  
	  // Return the list of devices for applications
	  @GET
	  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	  public List<Device> getDevices() {		  	
	    return manager.getDevices();
	  }	  
	  
	  // retuns the number of devices
	  // use {BASE_URL}/devices/count
	  @GET
	  @Path("count")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getCount() {
		  int deviceCount = manager.getDeviceCount();
		  return String.valueOf(deviceCount);
	  }	  
	  
	  // Defines that the next path parameter after devices is
	  // treated as a parameter and passed to the DeviceResource
	  // use {BASE_URL}/devices/1
	  // 1 will be treated as parameter resource and passed to DeviceResource
	  @Path("{device}")
	  public DeviceResource getDevice(@PathParam("device") String id) {
	    return new DeviceResource(uriInfo, request, id);
	  }	  
	  
}
