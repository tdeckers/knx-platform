package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class MessageToast extends PopupPanel {
	
	private int delay = 2000;
	
	public MessageToast(String message) {
		super(true);		
		setWidget(new Label(message));		
	}
	
	@Override
	public void show() {
		super.setPopupPosition(100, 100);
		super.show();
		Scheduler scheduler = Scheduler.get();
		
		scheduler.scheduleFixedDelay(new RepeatingCommand() {
			@Override
			public boolean execute() {
				MessageToast.this.hide();
				return false;
			}}, delay);
	}
	
	public static void message(String message) {
		final MessageToast toast = new MessageToast(message);
		toast.show();
	}
		
}
