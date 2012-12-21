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
import com.ducbase.knxplatform.config.ConfigManager;
import com.ducbase.knxplatform.devices.Device;
import com.ducbase.knxplatform.devices.DeviceManager;



public class GoogleTalkConnector implements ConnectionListener, ChatManagerListener, MessageListener {
	
	private static Logger logger = Logger.getLogger(GoogleTalkConnector.class.getName());
	
	private static GoogleTalkConnector instance;
	private Connection conn;
	private ConfigManager config = null;
	
	final static private String PIN_PROP = "talk.pin";
	final static private String SERVER_PROP = "talk.server";
	final static private String SERVICE_PROP = "talk.service";
	final static private String USERNAME_PROP = "talk.username";
	final static private String PASSWORD_PROP = "talk.password";
	final static private String RESOURCE_PROP = "talk.resource";
	
	private GoogleTalkConnector() throws XMPPException, IOException {
		config = ConfigManager.getInstance();
		String server = config.getProperty(SERVER_PROP);
		String service = config.getProperty(SERVICE_PROP);
		ConnectionConfiguration xmppconfig = new ConnectionConfiguration(server, 5222, service);
		logger.fine("XMPP server: " + server + ", service: " + service);
		conn = new XMPPConnection(xmppconfig);
		conn.connect();
		String username = config.getProperty(USERNAME_PROP);
		String password = config.getProperty(PASSWORD_PROP);
		String resource = config.getProperty(RESOURCE_PROP);
		logger.fine("XMPP username: " + username + ", resource: " + resource);
		conn.login(username, password, resource);
		conn.addConnectionListener(this);
		conn.getChatManager().addChatListener(this);
	}
	
	public static void initialize() throws XMPPException, IOException {
		if (instance == null) {
			synchronized(GoogleTalkConnector.class) {
				if (instance == null) {
					instance = new GoogleTalkConnector();
				}
			}
		}
	}
	
	public static GoogleTalkConnector getInstance() throws XMPPException, IOException {
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
		String pin = config.getProperty(PIN_PROP);
		String text = message.getBody();
		if (text.equalsIgnoreCase("gate " + pin)) {
			DeviceManager deviceManager = DeviceManager.getInstance();
			Device device = deviceManager.getDevice("B7"); // B7 is gate.
			((KNXSwitched) device).turnOn();
			try {Thread.sleep(500);} catch(Exception e) {};
			((KNXSwitched) device).turnOff();
			logger.info("Gate opened");
		} else if (text.toLowerCase().contains("alarm")) {
			DeviceManager deviceManager = DeviceManager.getInstance();
			KNXSwitched alarm = (KNXSwitched) deviceManager.getDevice("B2");
			KNXSwitched alarmPartial = (KNXSwitched) deviceManager.getDevice("B3");
			try {
				if (alarm.isOn()) {
					chat.sendMessage("Alarm armed!");
				} else if (alarmPartial.isOn()) {
					chat.sendMessage("Alarm is partially armed!");
				} else {
					chat.sendMessage("Alarm is not armed.");
				}
			} catch(XMPPException e) {
				logger.warning("Failed to respond: " + e.getMessage());
			}
		} else {
			try {
				chat.sendMessage("Huh?");
			} catch (XMPPException e) {
				logger.warning("Failed to respond: " + e.getMessage());
			}
		}
	}	

	
}
