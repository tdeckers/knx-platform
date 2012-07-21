package com.ducbase.knxplatform.adapters;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Access KNX data through a REST API
 * @author tom@ducbase.com
 *
 */
@Path("/knxservice")
public class KNXService {

	@Context
	ServletContext context;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getValueFromGroupAddress(@QueryParam("address") String groupAddress) {
		KNXAdapter adapter = (KNXAdapter) context.getAttribute("adapter");
		return adapter.getValueForGroupAddress(groupAddress);
	}
	
}
