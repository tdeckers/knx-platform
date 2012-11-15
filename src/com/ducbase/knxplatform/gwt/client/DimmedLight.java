package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class DimmedLight extends Composite implements ClickHandler {

	SwitchedLight light = new SwitchedLight();
	Label label = new Label();
	
	public DimmedLight() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.add(light);		
		panel.add(label);
		light.addClickHandler(this);		
		label.setText("0 %");
		
		initWidget(panel);
		
		setStyleName("knx-dimmedlight");
	}
	
	public void setSize(String width, String height) {
		light.setSize(width, height);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if (light.isOn()) {
			label.setText("100 %");			
		} else {
			label.setText("0 %");
		}
	}

}
