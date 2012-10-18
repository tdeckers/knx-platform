package com.ducbase.knxplatform;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

public class KNXWebSocketServlet extends WebSocketServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(KNXWebSocketServlet.class.getName());
	WebSocketManager manager;
	
	public KNXWebSocketServlet() {
		logger.fine("Creating KNXWebSocketServlet");
		manager = new WebSocketManager();
	}	
	
	@Override
	protected StreamInbound createWebSocketInbound(String protocol) {
		logger.fine("Creating new Web Socket");
		UIMessage inbound = new UIMessage(manager);
		return inbound;
	}

}
