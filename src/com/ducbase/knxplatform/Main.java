package com.ducbase.knxplatform;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String sayHi() {
		return "Hi!"; 
		
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
