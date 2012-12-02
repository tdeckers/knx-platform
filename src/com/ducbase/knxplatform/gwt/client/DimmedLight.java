package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class DimmedLight extends Device implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, 
												TouchStartHandler, TouchMoveHandler, TouchEndHandler  {

	SwitchedLight light = new SwitchedLight();
	Label label = new Label();
	private int dimValue = 0; // 0-100 
	
	public DimmedLight() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		if (light.clickHandler != null) {  		// Remove click handler from light
			light.clickHandler.removeHandler(); // We implement our own handlers here.
		}
		
		panel.add(light);
		panel.add(label);
		
		light.addMouseDownHandler(this);
		light.addMouseMoveHandler(this);		
		light.addMouseUpHandler(this);
		
		light.addTouchStartHandler(this);	
		light.addTouchMoveHandler(this);
		light.addTouchEndHandler(this);
		
		this.setDimValue(0);		
		
		initWidget(panel);
		
		setStyleName("knx-dimmedlight");
	}
	
	@Override
	public void setId(String id) {
		super.setId(id);
		light.setId(id);
	}
	
	public void setSize(String width, String height) {
		light.setSize(width, height);
	}
	
	public int getDimValue() {
		return dimValue;
	}

	/**
	 * set dimValue.
	 * 
	 * @param dimValue new dim value.  If dimValue is greater than 100, dimValue is set to 100. If dimValue is smaller than
	 * 0, dimValue is set to 0.
	 */
	public void setDimValue(int dimValue) {
		if (dimValue > 100) {
			dimValue = 100;
		}
		if (dimValue < 0) {
			dimValue = 0;
		}
		this.dimValue = dimValue;
		label.setText(dimValue + " %");
	}
	
//	@Override
//	public void onDoubleClick(DoubleClickEvent event) {
//		DimPopup popup = new DimPopup(dimValue);
//		popup.showRelativeTo((UIObject) event.getSource());
//	}	
	
	// Admin variables
	boolean mouseMoved = false;
	boolean clickStart = false;
	int startX;
	int startY;
	int startDimValue;
	double velocity = 1;
	
	@Override
	public void onMouseDown(MouseDownEvent event) {
		GWT.log("mouse down");
		processStart(event);
	}	
	
	@Override
	public void onTouchStart(TouchStartEvent event) {
		GWT.log("touch start");
		processStart(event);
	}	
	
	private void processStart(DomEvent event) {
		event.preventDefault();  // Prevent image drag behavior
		event.stopPropagation();
		
		// reset values
		mouseMoved = false;		
		clickStart = true; // start the logic.
		startDimValue = dimValue;
		if (event instanceof MouseEvent) {
			MouseEvent mEvent = (MouseEvent) event;
			startX = mEvent.getClientX();
			startY = mEvent.getClientY();	
		}
		if (event instanceof TouchEvent) {
			TouchEvent tEvent = (TouchEvent) event;
			JsArray<Touch> touches = tEvent.getTouches();
			Touch touch = touches.get(0);
			if (touch != null) {
				startX = touch.getClientX();
				startY = touch.getClientY();
			}
		}
		
		// Capture all mouse events uptil mouseUp.  Without this, we loose capture when the cursor
		// moves outside the light image.
		DOM.setCapture(light.getElement());
	}
	
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		//GWT.log("mouse move");
		processMove(event);
	}
	
	@Override
	public void onTouchMove(TouchMoveEvent event) {
		//GWT.log("touch move");
		processMove(event);
	}	
	
	private void processMove(DomEvent event) {  // TODO on touch, show large label with dimValue
		if(clickStart) {
			mouseMoved = true;
			
			int x = 0;
			int y = 0;
			if (event instanceof MouseEvent) {
				MouseEvent mEvent = (MouseEvent) event;
				x = mEvent.getClientX();
				y = mEvent.getClientY();	
			}
			if (event instanceof TouchEvent) {
				TouchEvent tEvent = (TouchEvent) event;
				JsArray<Touch> touches = tEvent.getTouches();
				Touch touch = touches.get(0);
				if (touch != null) {
					x = touch.getClientX();
					y = touch.getClientY();
				}
				velocity = 0.25; // touches seem to be more sensitive, dial down the action.
			}			
			
			// Calculate distance
			double distance = Math.sqrt(Math.pow(Math.abs(x - startX), 2) + Math.pow(Math.abs(y - startY), 2));
			// Calculate direction
			int up = 0;
			if ( (x > startX) && (y < startY) ) {
				up = 1;
			} else if ( (x < startX) && (y > startY) ) {
				up = -1;
			}
			//GWT.log("Distance: " + distance + " (" + up + ")");
			
			int dimDiff = (int) Math.round(distance * velocity);
			
			if (up != 0) {				
				setDimValue(startDimValue + up * dimDiff);
			}
		}
	}
	
	@Override
	public void onMouseUp(MouseUpEvent event) {
		GWT.log("mouse up");
		processEnd(event);
	}
	
	@Override
	public void onTouchEnd(TouchEndEvent event) {
		GWT.log("touch end");
		processEnd(event);
		
	}

	private void processEnd(DomEvent event) {
		if (!mouseMoved) {
			light.processClick();
		} else {
			// we moved.  Sent updated value.  We can use this.getDimValue since we set it in mousemove.
			String data = "{'dimValue': '" + this.getDimValue() + "'}";
			ServiceClient.sendData(data, this.getUrl());  			
		}
		
		clickStart = false;
		// release capture when done.
		DOM.releaseCapture(light.getElement());
	}




	@Override
	public void update(String json) {
		light.update(json);
		DimmedLightVO vo = JsonUtils.safeEval(json);
		this.setDimValue(vo.getDimValue());
		
	}
	
	static public class DimmedLightVO extends JavaScriptObject {
		
		// Overlay types always have protected, zero argument constructors.
		protected DimmedLightVO() {			
		}
		
		// JSNI methods to get stock data.
		public final native boolean isOn() /*-{ return this.on == "true"; }-*/;
		public final native int getDimValue() /*-{ return parseInt(this.dimValue); }-*/;
		
	}

}
