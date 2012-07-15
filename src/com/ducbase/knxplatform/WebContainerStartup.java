package com.ducbase.knxplatform;

import java.io.File;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import tuwien.auto.calimero.log.LogManager;
import tuwien.auto.calimero.log.LogService;



public class WebContainerStartup implements ServletContextListener {
	
	private static Logger logger = Logger.getLogger(ServletContextListener.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("Context Initialized");
		KNXAdapter adapter = new KNXAdapter();
		adapter.start();
		event.getServletContext().setAttribute("adapter", adapter);
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		logger.info("Context Destroyed");
		KNXAdapter adapter = (KNXAdapter) event.getServletContext().getAttribute("adapter");
		adapter.stop();
		
		// Manually close all LogServices... Tomcat recognizes hung threads when reloading app.
		// TODO: Bug in calimero code?
		// TODO: Update - this code doesn't work.  Dispatcher thread in LogService has a method
		// called quit which is never called.
		String[] names = LogManager.getManager().getAllLogServices();
		for(String name: names) {
			logger.warning("Removing log services (KNX)");
			LogService service = LogManager.getManager().getLogService(name);
			service.removeAllWriter(true); // this will close them.
			LogManager.getManager().removeLogService(name);
		}
				
	}

}
