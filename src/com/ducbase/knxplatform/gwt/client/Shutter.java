package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Shutter extends Device implements ClickHandler {
	
	Image window = new Image("img/window.png");
	Image grey = new Image("img/grey.jpg");
	private String imageUp = "img/up.png";
	private String imageStop = "img/stop.png";
	private String imageDown = "img/down.png";

	private int position;
	
	public Shutter() {
		AbsolutePanel panel = new AbsolutePanel();
		window.addClickHandler(this);
		panel.add(window, 0, 0);
		grey.addClickHandler(this);
		panel.add(grey, 10, 11);
		
		initWidget(panel);
		
		setStyleName("knx-shutter");
	}
	
	@Override 
	public void setSize(String width, String height) {
		super.setSize(width, height);
		window.setSize(width, height);
		grey.setSize("19px", "0px"); // shutter open (0), closed (21).
		
	}
	
	public void setPosition(int position) {
		this.position = position;
		grey.setHeight(21 * position / 100 + "px");
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == window || event.getSource() == grey) {
			int x = event.getClientX();
			int y = event.getClientY();
			CommandPopup popup = new CommandPopup();
			popup.setPopupPosition(x + 20, y - 40);
			popup.show();
		}
		
	}

	@Override
	public void update(String json) {
		ShutterVO vo = JsonUtils.safeEval(json);
		this.setPosition(vo.getPosition());

	}
	
	static public class ShutterVO extends JavaScriptObject {
		
		// Overlay types always have protected, zero argument constructors.
		  protected ShutterVO() {}
		  
		// JSNI methods to get stock data.
		  public final native int getPosition() /*-{ return parseInt(this.position); }-*/; 	
	  
	}	
	
	public class CommandPopup extends PopupPanel implements ClickHandler {
		
		Image upImage = new Image(imageUp);
		Image downImage = new Image(imageDown);
		Image stopImage = new Image(imageStop);
		
		public CommandPopup() {			
			super(true);
			VerticalPanel panel = new VerticalPanel();
			
			upImage.setSize("30px", "30px");
			upImage.addClickHandler(this);
			panel.add(upImage);
			
			stopImage.setSize("30px", "30px");
			stopImage.addClickHandler(this);
			panel.add(stopImage);
			
			downImage.setSize("30px", "30px");
			downImage.addClickHandler(this);
			panel.add(downImage);
			
			setWidget(panel);		
			
			setStyleName("knx-shutter-popup");
		}

		@Override
		public void onClick(ClickEvent event) {
			
			if (event.getSource() == upImage) {
				processClick("up");
			}
			if (event.getSource() == stopImage) {
				processClick("stop");
			}
			if (event.getSource() == downImage) {
				processClick("down");
			}
		}
		
		void processClick(String command) {
			CommandPopup.this.hide();
			
			// Send update to server!
			String data = "{'upDown': '" + command + "'}";
			ServiceClient.sendData(data,Shutter.this.getUrl());
		}
	}

}
