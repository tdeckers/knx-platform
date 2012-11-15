package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {

	@Source("com/ducbase/knxplatform/gwt/forall/light_bulb_on.png")
	ImageResource onLight();
	
	@Source("com/ducbase/knxplatform/gwt/forall/light_bulb_off.png")
	ImageResource offLight();

	@Source("com/ducbase/knxplatform/gwt/forall/knx.css")
	CssResource knxStylesheet();

}
