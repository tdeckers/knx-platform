package com.ducbase.knxplatform.api;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.ducbase.knxplatform.adapters.devices.DeviceException;
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
		  Device device = manager.getDevice(id);
		  if ( device == null )
			  throw new WebApplicationException(Status.NOT_FOUND);
		  return device;
	  }
	  
	  //Application integration     
	  @GET
	  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	  public Device getDevice() {
		  Device device = manager.getDevice(id);
		  if ( device == null )
			  throw new WebApplicationException(404); // NOT FOUND
		  return device;
	  }
	  
	  // updates to devices (e.g. switch lights)
	  @PUT
	  @Consumes({MediaType.APPLICATION_JSON})
	  public void updateDevice(JSONObject object) {
		  Device device = manager.getDevice(id);
		  if (device == null )
			  throw new WebApplicationException(Status.NOT_FOUND);
		  try {
			  device.update(object);
		  } catch (DeviceException e) {
			  logger.warning(e.getMessage() + " --- " + object.toString());
			  throw new WebApplicationException(e, Status.BAD_REQUEST);
		  }  
	  }
	  	
	  // delete devices - NOT SUPPORTED
	  @DELETE
	  public void deleteNotImplemented() {
		  throw new WebApplicationException(Status.fromStatusCode(501));
	  }
	  
	  // post devices - NOT SUPPORTED
	  @POST
	  public void postNotImplemented() {
		  throw new WebApplicationException(Status.fromStatusCode(501));
	  }
	  
	  
}
