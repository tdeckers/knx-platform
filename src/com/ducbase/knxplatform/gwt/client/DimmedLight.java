package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class DimmedLight extends Composite implements ClickHandler, DoubleClickHandler, TouchMoveHandler {

	SwitchedLight light = new SwitchedLight();
	Label label = new Label();
	Label leftArrow = new Label();
	Label rightArrow = new Label();
	private int dimValue = 0; // 0-100 
	
	public DimmedLight() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		leftArrow.setText("<<");
		rightArrow.setText(">>");
		
		panel.add(leftArrow); // TODO: add click handlers
		panel.add(light);
		panel.add(label);
		panel.add(rightArrow);  // TODO: add click handlers
		
		light.addClickHandler(this);
		//light.addTouchMoveHandler(this);
		this.setDimValue(0);		
		
		initWidget(panel);
		
		setStyleName("knx-dimmedlight");
	}
	
	public void setSize(String width, String height) {
		light.setSize(width, height);
	}
	
	public int getDimValue() {
		return dimValue;
	}

	public void setDimValue(int dimValue) {
		this.dimValue = dimValue;
		label.setText(dimValue + " %");
	}

	@Override
	public void onClick(ClickEvent event) {
		if (light.isOn()) {
			this.setDimValue(100);
		} else {
			this.setDimValue(0);
		}
	}
	
	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		
	}	

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		JsArray<Touch> touches = event.getChangedTouches();
		int y = touches.get(0).getClientY();

		// TODO: figure this out!!!
		// only works on mobile devices / touch interfaces.
		// label.setText("Y: " + y);		
	}

	class DimmerPopupPanel extends PopupPanel {
		
		public DimmerPopupPanel() {
			super(true); // enable auto-hide
			
		}
	}

}
