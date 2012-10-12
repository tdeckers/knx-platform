package com.ducbase.knxplatform.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Access KNX data through a REST API
 * @author tom@ducbase.com
 *
 */
@Path("/knxservice")
public class KNXService {
	private static Logger logger = Logger.getLogger(KNXService.class.getName());

	@Context
	ServletContext context;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("group")
	public String getValueForGroupAddress(@QueryParam("address") String groupAddress) {
		if ( (groupAddress == null) || "".equals(groupAddress)) {
			return "{ \"value\": \"n/a\" }";
		}
		KNXAdapter adapter = (KNXAdapter) context.getAttribute("adapter");
		String value = adapter.getValueForGroupAddress(groupAddress);
		return "{ \"value\": \"" + value + "\" }";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)	
	@Path("state")
	public String getState() {
		KNXAdapter adapter = (KNXAdapter) context.getAttribute("adapter");
		return "{ \"value\": \"" + adapter.getState() +"\" }";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)	
	@Path("lastconnect")
	public String getLastConnect() {
		KNXAdapter adapter = (KNXAdapter) context.getAttribute("adapter");
		return "{ \"value\": \"" + adapter.getLastConnect() +"\" }";
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("groups")
	public String getValueForGroupAddresses() {
		String[] list = {"1/4/20"};
		List<String> groupAddresses = Arrays.asList(list);
		
		KNXAdapter adapter = (KNXAdapter) context.getAttribute("adapter");
		StringBuffer result = new StringBuffer();
		result.append("{ \"groups\": [ ");
		Iterator<String> iter = groupAddresses.iterator();
		boolean first = true;
		while(iter.hasNext()) {
			if (!first) {
				result.append(',');
			}
			String address = iter.next();
			String value = adapter.getValueForGroupAddress(address);
			result.append(" { \"" + address + "\": \"" + value + "\" }");
			
			first = false;  // After reaching here once, first pass is over.
		}
		result.append(" ] }");
		
		return result.toString();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("group")
	public void send(JSONObject object) throws JSONException {
		String groupAddress = object.getString("address");
		if (groupAddress == null || "".equals(groupAddress)) {
			logger.warning("groupAddress can't be empty (" + groupAddress + ")");
			return;
		}
		KNXAdapter adapter = (KNXAdapter) context.getAttribute("adapter");
		
		try {
			boolean value = object.getBoolean("boolean");
			logger.fine("About to send boolean (" + value + ")");
			adapter.sendBoolean(groupAddress, value);
			return;
		} catch (JSONException e) {
			// likely 'boolean' not found.
		}
		
		try {
			int value = object.getInt("int");
			logger.fine("About to send int (" + value + ")");			
			adapter.sendIntUnscaled(groupAddress, value);	
			return;
		} catch (JSONException e) {
			// likely 'int' not found.
		}
		
		// Should not get here
		throw new RuntimeException("Nothing to send");
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("log")	
	public void setLog(JSONObject object) throws JSONException {
		String level = object.getString("level");
				
		LogManager mgr = LogManager.getLogManager();
		Logger confLog = mgr.getLogger("com.ducbase.knxplatform");
		
		if ("finer".equals(level)) {
			logger.info("Setting log to FINER");
			confLog.setLevel(Level.FINER);			
		} else {
			logger.info("Setting log to INFO");			
			confLog.setLevel(Level.INFO);
		}
	}
	
}
