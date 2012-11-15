package com.ducbase.knxplatform.gwt.client.ws;

public interface MessageHandler {
	
	public void onOpen(WebSocket socket);
	public void onClose(WebSocket socket);
	public void onError(WebSocket socket);
	public void onMessage(WebSocket socket, MessageEvent event);

}
