package com.ducbase.knxplatform;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.ducbase.knxplatform.config.ConfigManager;



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
		
		try {
			ConfigManager.loadDevices();
		} catch (JAXBException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
