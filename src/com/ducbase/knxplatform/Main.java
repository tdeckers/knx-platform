package com.ducbase.knxplatform;

import java.awt.List;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.ducbase.knxplatform.adapters.KNXAdapter;



/**
 * Main class for starting the platform
 * 
 * @author tom@ducbase.com
 *
 */
@Path("/hello")
public class Main {
	
	private static Logger logger = Logger.getLogger(Main.class.getName());
	
	@Context
	ServletContext context;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String sayHi() {
		logger.fine(" === Hi! === ");
		return "Hi!"; 		
	}
	
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public void send(String value) {
		logger.fine("Sending...");
		KNXAdapter adapter = (KNXAdapter) context.getAttribute("adapter");
		boolean boolValue = Boolean.parseBoolean(value);		
		adapter.send(boolValue);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("Starting");
		
		// load workflows
		// load connectors
		//   load Google docs connector
		// start adapters
		KNXAdapter knx = new KNXAdapter();
		knx.start();
		
		//	 KNX adapter starts cache		
		// load scheduling
		
	}
	
	
	

}
