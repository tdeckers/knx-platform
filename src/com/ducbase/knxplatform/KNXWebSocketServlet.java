package com.ducbase.knxplatform;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

public class KNXWebSocketServlet extends WebSocketServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(KNXWebSocketServlet.class.getName());
	WebSocketManager manager;
	
	public KNXWebSocketServlet() {
		logger.fine("Creating KNXWebSocketServlet");
		manager = WebSocketManager.getInstance();
	}	
	
	@Override
	protected StreamInbound createWebSocketInbound(String protocol,
			HttpServletRequest request) {
		logger.fine("Creating new Web Socket");
		UIMessage inbound = new UIMessage(manager);
		return inbound;
	}

}
