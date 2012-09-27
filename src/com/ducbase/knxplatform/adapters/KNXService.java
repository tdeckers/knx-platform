package com.ducbase.knxplatform.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
	public void sendBoolean(JSONObject object) throws JSONException {
		String groupAddress = object.getString("address");
		boolean value = object.getBoolean("value");
		
		if (groupAddress == null || "".equals(groupAddress)) {
			logger.warning("groupAddress can't be empty (" + groupAddress + "|" + value + ")");
			return;
		}
		KNXAdapter adapter = (KNXAdapter) context.getAttribute("adapter");
		adapter.sendBoolean(groupAddress, value);		
	}
	
}
