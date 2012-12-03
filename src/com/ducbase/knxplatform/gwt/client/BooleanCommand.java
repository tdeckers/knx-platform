package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BooleanCommand extends Device implements ClickHandler {

	Image image = new Image();
	String imageUrl = "img/lock_closed.png";
	
	String code;
	
	public BooleanCommand() {
		image.setUrl(imageUrl);
		image.addClickHandler(this);
		
		initWidget(image);
		
		setStyleName("knx-booleancommand");
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	private boolean isProtected() {
		return (this.code != null) && !"".equals(this.code); 
	}
	
	public void setSize(String width, String height) {
		image.setSize(width, height);
	}	
	
	@Override
	public void onClick(ClickEvent event) {
		event.preventDefault();
		event.stopPropagation();
		
		if (isProtected()) {
			ProtectPopup popup = new ProtectPopup();
			popup.showRelativeTo(this);
		} else {
			this.sendCommand();
		}

	}
	
	private void sendCommand() {
		// Send update!
		 String dataOn = "{'on': '" + true + "'}";
		 ServiceClient.sendData(dataOn, this.getUrl());	
		 
		 Timer timer = new Timer() {
			@Override
			public void run() {
				 String dataOff = "{'on': '" + false + "'}";
				 ServiceClient.sendData(dataOff, BooleanCommand.this.getUrl());					
			}
		 };
		 timer.schedule(350); // send 'off' command a bit later.
		 
		 
	}

	@Override
	public void update(String json) {
		// ignore.  Command is write-only.
	}
	
	public class ProtectPopup extends PopupPanel implements ClickHandler {

		Image image = new Image();
		TextBox text = new TextBox();
		
		public ProtectPopup() {
			super(true);
			VerticalPanel panel = new VerticalPanel();
			panel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
			panel.add(text);
			panel.add(image);
			
			image.setUrl(imageUrl);
			image.addClickHandler(this);
			
			setWidget(panel);	
			
			setStyleName("knx-booleancommand-popup");
		}
		
		@Override
		public void onClick(ClickEvent event) {
			if (text.getText().equals(BooleanCommand.this.getCode())) {
				sendCommand();
			} else {
				this.addStyleDependentName("alert");
				Timer timer = new Timer() {
					@Override
					public void run() {
						ProtectPopup.this.removeStyleDependentName("alert");
					}					
				};
				timer.schedule(750);
			}
		}
		
	}

}
