package com.ducbase.knxplatform.scheduling;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.connectors.GoogleDriveConnector;
import com.google.gdata.util.ServiceException;

public class GoogleUploadJob implements Job {
	
	GoogleDriveConnector connector;
	
	private static Logger logger = Logger.getLogger(GoogleUploadJob.class.getName());
	
	public GoogleUploadJob() {

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		KNXAdapter adapter = null;
		GoogleDriveConnector connector = null;
		try {
			adapter = (KNXAdapter) context.getScheduler().getContext().get("adapter");
			connector = (GoogleDriveConnector) context.getScheduler().getContext().get("connector");
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		String[] addresses = {"2/6/1", "2/6/0", "2/1/0", "2/2/0", "2/1/9", "2/2/9", "2/0/4"};
		for(String address: addresses) {
			map.put('h' + address.replace('/', 'w'), adapter.getValueForGroupAddress(address));
		}
		
//		String outsideActualTemp = adapter.getValueForGroupAddress("2/6/1");
//		String ligingFloorTemp = adapter.getValueForGroupAddress("2/6/0");
//		String livingActualTemp = adapter.getValueForGroupAddress("2/1/0");
//		String livingSetpointTemp = adapter.getValueForGroupAddress("2/2/0");
//		
//		String badkamerActualTemp = adapter.getValueForGroupAddress("2/1/9");
//		String badkamerSetpointTemp = adapter.getValueForGroupAddress("2/2/9");
//		
//		String heatingrequired = adapter.getValueForGroupAddress("2/0/4"); 				

		logger.fine("Uploading...");
		
		try {
			connector.upload(map);
		} catch (IOException | ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JobExecutionException(e);
		}
	}

}
