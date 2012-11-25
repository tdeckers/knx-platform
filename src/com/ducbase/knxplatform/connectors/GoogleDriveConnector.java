package com.ducbase.knxplatform.connectors;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
 * @see https://developers.google.com/google-apps/spreadsheets
 * @see https://code.google.com/apis/console/#project:775318026373
 * @see https://code.google.com/p/google-api-java-client/wiki/OAuth2#Service_Accounts
 * 
 * @author tom@ducbase.com
 *
 */
public class GoogleDriveConnector {
	
	private static Logger logger = Logger.getLogger(GoogleDriveConnector.class.getName());
	
	private static GoogleDriveConnector instance;
	
	private SpreadsheetService service = new SpreadsheetService("KNXPlatform");
	
	private GoogleDriveConnector() throws GeneralSecurityException, IOException, ServiceException {
		File keyFile = new File("key.p12"); // typically this is at {user.dir}/<keyfilename>
		logger.info("Looking for key at " + keyFile.getAbsolutePath());
		
		// TODO: key file hardcoded, and place in d:\dev\eclipse37.  Should read from somewhere else.
		GoogleCredential credential = new GoogleCredential.Builder()
			.setTransport(new NetHttpTransport())
			.setJsonFactory(new JacksonFactory())
			.setServiceAccountId("775318026373@developer.gserviceaccount.com")  // share doc with this user!
			.setServiceAccountScopes("https://spreadsheets.google.com/feeds/")
			.setServiceAccountPrivateKeyFromP12File(keyFile)
			.build();
				
		service.setOAuth2Credentials(credential);
	}
	
	public static void initialize() throws GeneralSecurityException, IOException, ServiceException {
		if (instance == null) {
			synchronized(GoogleDriveConnector.class) {
				if (instance == null) {
					instance = new GoogleDriveConnector();
				}
			}
		}
	}
	
	public static GoogleDriveConnector getInstance() throws GeneralSecurityException, IOException, ServiceException {
		initialize();
		return instance;
		
	}	

	public void upload(HashMap<String, String> values) throws IOException, ServiceException {
		// select spreadsheet and worksheet
		URL listFeedUrl = selectSheet(service);
		
		 // Create a local representation of the new row.
	    ListEntry row = new ListEntry();
	    Set<String> keys = values.keySet();
	    
	    for(String key: keys) {
	    	logger.fine("KEY[" + key + "]:VALUE[" + values.get(key) + "]");
	    	row.getCustomElements().setValueLocal(key, values.get(key));
	    }
	    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  // need to make sure this matches with sheet locale.
	    row.getCustomElements().setValueLocal("date", formatter.format(new Date()));
	    
	    // Send the new row to the API for insertion.
	    row = service.insert(listFeedUrl, row);
		
	}

	private URL selectSheet(SpreadsheetService service) throws IOException, ServiceException {
		// Define the URL to request.  This should never change.
	    URL SPREADSHEET_FEED_URL = null;
		try {
			SPREADSHEET_FEED_URL = new URL(
			    "https://spreadsheets.google.com/feeds/spreadsheets/private/full");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// This should not happen... hardcoded URL!
		}
		
		// Make a request to the API and get all spreadsheets.
	    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
	        SpreadsheetFeed.class);
	    List<SpreadsheetEntry> spreadsheets = feed.getEntries();

	    logger.fine("Got " + spreadsheets.size() + " spreadsheet(s).");
	    
	    if (spreadsheets.size() == 0) {
	        // TODO: There were no spreadsheets, act accordingly.
	    }
	    
	    // What sheet are we looking for?
	    // We use the model: knx_<month><year>
	    SimpleDateFormat formatter = new SimpleDateFormat("wwYYYY");
	    String sheetName = "knx_" + formatter.format(new Date());
	    
	    logger.fine("Looking for spreadsheet [" + sheetName + "]");
	    
	    Iterator<SpreadsheetEntry> iterator = spreadsheets.iterator();
	    SpreadsheetEntry spreadsheet = null;
	    while (iterator.hasNext()) {
	    	spreadsheet = iterator.next();
	    	if (sheetName.equals(spreadsheet.getTitle().getPlainText())) {
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
		return listFeedUrl;
	}
	
}
