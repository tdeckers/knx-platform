package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;

public class DimmedLight extends Device implements ClickHandler, DoubleClickHandler  {

	SwitchedLight light = new SwitchedLight();
	Label label = new Label();
	private int dimValue = 0; // 0-100 
	
	public DimmedLight() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		panel.add(light);
		panel.add(label);
		light.addDoubleClickHandler(this);
		label.addDoubleClickHandler(this);
		
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

	public void setDimValue(int dimValue) {
		this.dimValue = dimValue;
		label.setText(dimValue + " %");
	}
	
	@Override
	public void onClick(ClickEvent event) {

	}
	
	class DimmerPopupPanel extends PopupPanel {
		
		public DimmerPopupPanel() {
			super(true); // enable auto-hide
			
		}
	}
	
	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		DimPopup popup = new DimPopup(dimValue);
		popup.showRelativeTo((UIObject) event.getSource());
	}	

	class DimPopup extends PopupPanel implements CloseHandler<PopupPanel> {
		
		Image minus = new Image("img/minus.png");
		Image plus = new Image("img/plus.png");
		Label label = new Label();
		
		int origDimValue; // Keep dimValue we started with.  Only on change need to update.
		int dimValue;
		
		public DimPopup(int dimValue) {
			super(true);
			
			this.origDimValue = dimValue;
			this.dimValue = dimValue;
			
			HorizontalPanel panel = new HorizontalPanel();
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

			minus.setPixelSize(30, 30);
			panel.add(minus);
			label.setText(dimValue + " %");
			label.setStyleName("knx-dimmedlight-popup-label");
			panel.add(label);
			plus.setPixelSize(30, 30);
			panel.add(plus);			

			minus.addClickHandler(new ClickHandler() {				
				@Override
				public void onClick(ClickEvent event) {
					decreaseDimValue();					
				}
			});
			
			plus.addClickHandler(new ClickHandler() {				
				@Override
				public void onClick(ClickEvent event) {
					increaseDimValue();
					
				}
			});
			
			this.addCloseHandler(this);
			
			setWidget(panel);		
			setStyleName("knx-dimmedlight-popup");
		}
		
		private void decreaseDimValue() {
			dimValue -= 10;
			if (dimValue < 0) { dimValue = 0; };
			label.setText(dimValue + " %");
		}
		
		private void increaseDimValue() {
			dimValue += 10;
			if (dimValue > 100) { dimValue = 100; };
			label.setText(dimValue + " %");
		}

		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			if (origDimValue != dimValue) {
				DimmedLight.this.setDimValue(dimValue);
				
				// Send update!
				String data = "{'dimValue': '" + dimValue + "'}";
				ServiceClient.sendData(data, DimmedLight.this.getUrl());
			}
		}
		
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
