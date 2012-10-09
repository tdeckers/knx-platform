package com.ducbase.knxplatform.connectors;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.gdata.client.AuthTokenFactory;
import com.google.gdata.client.AuthTokenFactory.AuthToken;
import com.google.gdata.client.GoogleService.SessionExpiredException;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * Connects to GoogleDrive for data exchange
 * 
 * @see https://code.google.com/apis/console/#project:775318026373
 * @see https://code.google.com/p/google-api-java-client/wiki/OAuth2#Service_Accounts
 * 
 * @author tom@ducbase.com
 *
 */
public class GoogleDriveConnector {
	
	private static Logger logger = Logger.getLogger(GoogleDriveConnector.class.getName());
	
	public GoogleDriveConnector() throws GeneralSecurityException, IOException, ServiceException { 
		// TODO: key file hardcoded, and place in d:\dev\eclipse37.  Should read from somewhere else.
		GoogleCredential credential = new GoogleCredential.Builder()
			.setTransport(new NetHttpTransport())
			.setJsonFactory(new JacksonFactory())
//			.setServiceAccountId("tom@ducbase.com")
			.setServiceAccountId("775318026373@developer.gserviceaccount.com")  // share doc with this user!
//			.setServiceAccountScopes(DriveScopes.DRIVE)
			.setServiceAccountScopes("https://spreadsheets.google.com/feeds/")
			.setServiceAccountPrivateKeyFromP12File(new File("key.p12"))
			.build();
		
//		Drive drive = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
//				.setApplicationName("KNXPlatform")
//				.build();

		
		SpreadsheetService service = new SpreadsheetService("KNXPlatform");

		service.setOAuth2Credentials(credential);

		
		// Define the URL to request.  This should never change.
	    URL SPREADSHEET_FEED_URL = new URL(
	        "https://spreadsheets.google.com/feeds/spreadsheets/private/full");
		
		// Make a request to the API and get all spreadsheets.
	    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
	        SpreadsheetFeed.class);
	    List<SpreadsheetEntry> spreadsheets = feed.getEntries();

	    logger.info("Got " + spreadsheets.size() + " spreadsheet(s).");
	    
	    if (spreadsheets.size() == 0) {
	        // TODO: There were no spreadsheets, act accordingly.
	    }
	    
	    Iterator<SpreadsheetEntry> iterator = spreadsheets.iterator();
	    SpreadsheetEntry spreadsheet = null;
	    while (iterator.hasNext()) {
	    	spreadsheet = iterator.next();
	    	if ("knx_102012".equals(spreadsheet.getTitle().getPlainText())) {
	    		break;
	    	} else {
	    		spreadsheet = null;
	    	}
	    }
	    
	    if (spreadsheet == null) {
	    	// TODO: fix this properly.
	    	throw new RuntimeException("No spreadsheet found!");
	    }
	    
	 // Get the first worksheet of the first spreadsheet.
	    WorksheetFeed worksheetFeed = service.getFeed(
	            spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
	    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
	    WorksheetEntry worksheet = worksheets.get(0);
	    
	 // Fetch the list feed of the worksheet.
	    URL listFeedUrl = worksheet.getListFeedUrl();
	    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
	    
	 // Create a local representation of the new row.
	    ListEntry row = new ListEntry();
	    row.getCustomElements().setValueLocal("firstname", "Joe");
	    row.getCustomElements().setValueLocal("lastname", "Smith");
	    row.getCustomElements().setValueLocal("age", new Date().toString());
	    row.getCustomElements().setValueLocal("height", "176");

	    // Send the new row to the API for insertion.
	    row = service.insert(listFeedUrl, row);
			
	}
	

}
