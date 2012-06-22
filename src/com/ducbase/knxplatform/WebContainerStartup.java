package com.ducbase.knxplatform;

import java.io.File;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ducbase.knxplatform.adapters.KNXAdapter;

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
	}



}
