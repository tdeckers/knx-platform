package com.ducbase.knxplatform.connectors;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.*;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.logging.Logger;

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
	
	public GoogleDriveConnector() throws GeneralSecurityException, IOException { 
		// TODO: key file hardcoded, and place in d:\dev\eclipse37.  Should read from somewhere else.
		GoogleCredential credential = new GoogleCredential.Builder()
			.setTransport(new NetHttpTransport())
			.setJsonFactory(new JacksonFactory())
			.setServiceAccountId("tom@ducbase.com")
			.setServiceAccountScopes(DriveScopes.DRIVE)
			.setServiceAccountPrivateKeyFromP12File(new File("key.p12"))
			.build();
		Drive drive = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
				.setApplicationName("KNXPlatform")
				.build();
		
	}
	

}
