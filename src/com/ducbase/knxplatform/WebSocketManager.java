package com.ducbase.knxplatform;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.apache.catalina.websocket.WsOutbound;

public class WebSocketManager {
	
	private static Logger logger = Logger.getLogger(WebSocketManager.class.getName());
	
	List<UIMessage> connections;
	AtomicInteger connectionIds = new AtomicInteger(0);
	String connectionPrefix = "GUEST_";
	
	private static WebSocketManager instance;
	
	private WebSocketManager() {
		connections = new ArrayList<UIMessage>();
	}
	
	public static WebSocketManager getInstance() {
		if (instance == null) {
			synchronized(WebSocketManager.class) {
				if (instance == null) {
					instance = new WebSocketManager();
				}
			}
		}
		return instance;
	}

	public void add(UIMessage uiMessage) {
		connections.add(uiMessage);
		
	}

	public void remove(UIMessage uiMessage) {
		connections.remove(uiMessage);
		
	}

	public void broadcast(CharBuffer message) throws IOException {
		for(UIMessage inbound: connections) {
			logger.fine("Broadcasting to " + inbound.getId());
			WsOutbound outbound = inbound.getWsOutbound();
			outbound.writeTextMessage(CharBuffer.wrap(message));
			outbound.flush();
		}
	}

	public String getNewId() {
		return connectionPrefix + connectionIds.getAndIncrement();
	}

	public void broadcast(String json) throws IOException {
		for(UIMessage inbound: connections) {
			logger.fine("Broadcasting to " + inbound.getId());
			WsOutbound outbound = inbound.getWsOutbound();
			outbound.writeTextMessage(CharBuffer.wrap(json.toCharArray()));
			outbound.flush();
		}		
	}

}
