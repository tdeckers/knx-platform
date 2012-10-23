package com.ducbase.knxplatform;

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
public class Main {
	
	private static Logger logger = Logger.getLogger(Main.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("Starting");
		
	}

}
