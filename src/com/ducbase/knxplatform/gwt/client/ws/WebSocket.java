package com.ducbase.knxplatform.gwt.client.ws;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @see http://code.google.com/p/gwt-comet/source/browse/trunk/src/net/zschech/gwt/websockets/client/WebSocket.java
 *
 */
public class WebSocket extends JavaScriptObject {
	
    public static final int CONNECTING = 0;
    public static final int OPEN = 1;
    public static final int CLOSED = 2;
    
    /**
     * Creates an WebSocket object.
     * 
     * @return the created object
     */
    public static native WebSocket create(String url) /*-{
            return new WebSocket(url);
    }-*/;
    
    public static native WebSocket create(String url, String protocol) /*-{
            return new WebSocket(url, protocol);
    }-*/;

    protected WebSocket() {
    }
    
    public final native int getReadyState() /*-{
            return this.readyState;
    }-*/;
    
    public final native int getBufferedAmount() /*-{
            return this.bufferedAmount;
    }-*/;

    public final native void send(String data) /*-{
            this.send(data);
    }-*/;
    
    public final native void close() /*-{
            this.close();
    }-*/;
    
    public final native void setOnOpen(MessageHandler handler) /*-{
    // The 'this' context is always supposed to point to the websocket object in the
    // onreadystatechange handler, but we reference it via closure to be extra sure.
    var _this = this;
    this.onopen = $entry(function() {
            handler.@com.ducbase.knxplatform.gwt.client.ws.MessageHandler::onOpen(Lcom/ducbase/knxplatform/gwt/client/ws/WebSocket;)(_this);
    });
}-*/;

public final native void setOnClose(MessageHandler handler) /*-{
    // The 'this' context is always supposed to point to the websocket object in the
    // onreadystatechange handler, but we reference it via closure to be extra sure.
    var _this = this;
    this.onclose = $entry(function() {
            handler.@com.ducbase.knxplatform.gwt.client.ws.MessageHandler::onClose(Lcom/ducbase/knxplatform/gwt/client/ws/WebSocket;)(_this);
    });
}-*/;

public final native void setOnError(MessageHandler handler) /*-{
    // The 'this' context is always supposed to point to the websocket object in the
    // onreadystatechange handler, but we reference it via closure to be extra sure.
    var _this = this;
    this.onerror = $entry(function() {
            handler.@com.ducbase.knxplatform.gwt.client.ws.MessageHandler::onError(Lcom/ducbase/knxplatform/gwt/client/ws/WebSocket;)(_this);
    });
}-*/;

public final native void setOnMessage(MessageHandler handler) /*-{
    // The 'this' context is always supposed to point to the websocket object in the
    // onreadystatechange handler, but we reference it via closure to be extra sure.
    var _this = this;
    this.onmessage = $entry(function(event) {
            handler.@com.ducbase.knxplatform.gwt.client.ws.MessageHandler::onMessage(Lcom/ducbase/knxplatform/gwt/client/ws/WebSocket;Lcom/ducbase/knxplatform/gwt/client/ws/MessageEvent;)(_this, event);
    });
}-*/;    

}
