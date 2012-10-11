package com.ducbase.knxplatform.scheduling;

import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.ducbase.knxplatform.adapters.KNXAdapter;
import com.ducbase.knxplatform.connectors.GoogleDriveConnector;

public class KNXAdapterCheckJob implements Job {
	
	private static Logger logger = Logger.getLogger(KNXAdapterCheckJob.class.getName());
	
	public KNXAdapterCheckJob() {
		
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		KNXAdapter adapter = null;		
		try {
			adapter = (KNXAdapter) context.getScheduler().getContext().get("adapter");
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		if(!adapter.isOk()) {
			adapter.connect();
		}

	}

}
