package com.ducbase.knxplatform;

import java.util.logging.Logger;
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
