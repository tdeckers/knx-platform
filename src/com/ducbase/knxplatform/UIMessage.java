package com.ducbase.knxplatform;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.logging.Logger;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

public class UIMessage extends MessageInbound {
	
	private static Logger logger = Logger.getLogger(UIMessage.class.getName());
	private WebSocketManager manager;
	
	private String id;

	public UIMessage(WebSocketManager manager) {
		this.manager = manager;
		this.id = manager.getNewId();
	}

	@Override
	protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
		//this application does not expect binary data
		throw new UnsupportedOperationException(
		"Binary message not supported.");
	}

	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
		logger.fine("Incoming: " + message);
		manager.broadcast(message);
	}
		
	@Override
	protected void onClose(int status) {
		logger.info("Closing WebSocket connection - " + id);
		manager.remove(this);
	}	
	
	@Override
	protected void onOpen(WsOutbound outbound) {
		logger.info("Opening WebSocket connection - " + id);
		manager.add(this);
	}

	public String getId() {
		return this.id;
	}

	
	
}
