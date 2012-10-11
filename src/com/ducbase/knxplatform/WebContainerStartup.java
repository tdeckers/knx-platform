package com.ducbase.knxplatform;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import tuwien.auto.calimero.log.LogManager;
import tuwien.auto.calimero.log.LogService;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.connectors.GoogleDriveConnector;
import com.ducbase.knxplatform.scheduling.GoogleUploadJob;
import com.ducbase.knxplatform.scheduling.KNXAdapterCheckJob;
import com.google.gdata.util.ServiceException;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;


public class WebContainerStartup implements ServletContextListener {
	
	private static Logger logger = Logger.getLogger(ServletContextListener.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("Initializing context");
		// TODO should probably set DeviceManager here instead.
		KNXAdapter adapter = new KNXAdapter();
		adapter.start();
		event.getServletContext().setAttribute("adapter", adapter);
		
		logger.info("Connecting to Google Drive");
		GoogleDriveConnector connector = null;
		try {
			connector = GoogleDriveConnector.getInstance();
		} catch (GeneralSecurityException | IOException | ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		try {
			SchedulerFactory factory = (SchedulerFactory) event.getServletContext().getAttribute("org.quartz.impl.StdSchedulerFactory.KEY");
			Scheduler scheduler = factory.getScheduler("GoogleScheduler");
			
			// Make sure we can use this KNX adapter and Connector in our Jobs
			scheduler.getContext().put("adapter", adapter);
			scheduler.getContext().put("connector", connector);

			// Job for sending data to Google Spreadsheet
			JobDetail googleJob = newJob(GoogleUploadJob.class)
					.withIdentity("google_job", "group1")
					.build();
			 
			Trigger googleTrigger = newTrigger()
					.withIdentity("google_trigger", "group1")
					.startNow()
					.withSchedule(simpleSchedule()
							.withIntervalInSeconds(300) // every 5 minutes
							.repeatForever())
					.build();
			
			logger.info("Scheduling Google job. [Scheduler: " + scheduler.getSchedulerName() + "]");
			scheduler.scheduleJob(googleJob, googleTrigger);	
			
			// Job for checking KNX adapter link
			JobDetail knxJob = newJob(KNXAdapterCheckJob.class)
					.withIdentity("knx_job", "group1")
					.build();
			
			Trigger knxTrigger = newTrigger()
					.withIdentity("knx_job", "group1")
					.startNow()
					.withSchedule(simpleSchedule()
							.withIntervalInSeconds(60) // every minute
							.repeatForever())
					.build();
			
			logger.info("Scheduling KNX job. [Scheduler: " + scheduler.getSchedulerName() + "]");
			scheduler.scheduleJob(knxJob, knxTrigger);				
				
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		logger.info("Destroying context");
		logger.info("Stopping KNX adapter");
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
