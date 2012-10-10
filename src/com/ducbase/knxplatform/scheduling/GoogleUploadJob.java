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
		String[] addresses = {"2/6/1", "2/6/0", // outside, floor (living)
				"2/1/0", "2/2/0", "2/3/0", // living (actual, setpoint, variable)
				"2/1/1", "2/2/1", "2/3/4", // bureau Marlies (actual, setpoint, variable)
				"2/1/2", "2/2/1", "2/3/5", // wasplaats (actual, setpoint, variable)
				"2/1/3", "2/2/3", "2/3/6", // entertainment (actual, setpoint, variable)
				"2/1/4", "2/2/4", "2/3/7", // kamer 1 (actual, setpoint, variable)
				"2/1/5", "2/2/5", "2/3/8", // douchekamer (actual, setpoint, variable)
				"2/1/6", "2/2/6", "2/3/9", // kamer 2 (actual, setpoint, variable)
				"2/1/7", "2/2/7", "2/3/10", // bureau Tom (actual, setpoint, variable)
				"2/1/8", "2/2/8", "2/3/12", // masterbedroom (actual, setpoint, variable)
				"2/1/9", "2/2/9", "2/3/13",  // badkamer (actual, setpoint, variable)
				"2/0/4" // heating required?
				};
		for(String address: addresses) {
			map.put("g" + address.replace('/', '-'), adapter.getValueForGroupAddress(address).replace('.', ','));
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
