package com.ducbase.knxplatform.connectors;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.ducbase.knxplatform.adapters.devices.KNXSwitched;
import com.ducbase.knxplatform.devices.Device;
import com.ducbase.knxplatform.devices.DeviceManager;



public class GoogleTalkConnector implements ConnectionListener, ChatManagerListener, MessageListener {
	
	private static Logger logger = Logger.getLogger(GoogleTalkConnector.class.getName());
	
	private static GoogleTalkConnector instance;
	private Connection conn;
	
	private GoogleTalkConnector() throws XMPPException {
		ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "ducbase.com");
		conn = new XMPPConnection(config);
		conn.connect();
		conn.login("knx@ducbase.com", "luke008+", "knxplatform");
		conn.addConnectionListener(this);
		conn.getChatManager().addChatListener(this);
	}
	
	public static void initialize() throws XMPPException {
		if (instance == null) {
			synchronized(GoogleTalkConnector.class) {
				if (instance == null) {
					instance = new GoogleTalkConnector();
				}
			}
		}
	}
	
	public static GoogleTalkConnector getInstance() throws XMPPException {
		initialize();
		return instance;		
	}		
	
	public void disconnect() {
		if (conn != null) {
			conn.disconnect();
		}
	}

	public void sendMessage(String message) throws XMPPException {
		if (conn == null || !conn.isConnected()) {
			logger.warning("Not connected.  Not sending message " + message);
			return;
		}
		
		Chat chat = conn.getChatManager().createChat("tom@ducbase.com", this);
		chat.sendMessage(message);
		logger.fine("Sent: " + message);
	}
	
	@Override
	public void connectionClosed() {
		logger.fine("Connection closed.");		
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		logger.fine("Connection closed: " + e.getMessage());		
	}

	@Override
	public void reconnectingIn(int seconds) {
		logger.fine("Reconnecting in " + seconds);		
	}

	@Override
	public void reconnectionFailed(Exception e) {
		logger.fine("Reconnection failed - " + e.getMessage());		
	}

	@Override
	public void reconnectionSuccessful() {
		logger.fine("Reconnection successful");		
	}

	@Override
	public void chatCreated(Chat chat, boolean createdLocally) {
		logger.fine("Chat created.");
		chat.addMessageListener(this);
	}

	@Override
	public void processMessage(Chat chat, Message message) {
		String text = message.getBody();
		if (text.equalsIgnoreCase("gate 6969")) {
			DeviceManager deviceManager = DeviceManager.getInstance();
			Device device = deviceManager.getDevice("B7"); // B7 is gate.
			((KNXSwitched) device).turnOn();
			try {Thread.sleep(500);} catch(Exception e) {};
			((KNXSwitched) device).turnOff();
			logger.info("Gate opened");
		}
	}	

	
}
