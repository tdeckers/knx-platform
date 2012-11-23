package com.ducbase.knxplatform.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.ducbase.knxplatform.gwt.client.ws.MessageEvent;
import com.ducbase.knxplatform.gwt.client.ws.MessageHandler;
import com.ducbase.knxplatform.gwt.client.ws.WebSocket;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class KNXUI implements EntryPoint {

	private Resources resources = GWT.create(Resources.class);
	private String baseUrl;
	private List<Device> devices = new ArrayList<Device>();
	
	@Override
	public void onModuleLoad() {

		// Configure base Url of the application.  Use to configure devices.
		// TODO: make this configurable if REST API is not with UI app.
		baseUrl = getBaseUrl(); 
		
		RootPanel rootPanel = RootPanel.get("container");
		rootPanel.getElement().getStyle().setPosition(Position.RELATIVE);
		rootPanel.setSize("600px", "700px");
		
		DecoratedTabPanel tabPanel = new DecoratedTabPanel();
		tabPanel.setAnimationEnabled(true);
		rootPanel.add(tabPanel, 0, 0);
		tabPanel.setSize("630px", "700px");
		
		AbsolutePanel absolutePanel_2 = new AbsolutePanel();
		tabPanel.add(absolutePanel_2, "Kelder", false);
		absolutePanel_2.setSize("580px", "707px");
		
		Image image_29 = new Image("img/floor-1.png");
		absolutePanel_2.add(image_29, 0, 0);
		image_29.setSize("580px", "");
		
		Image image_42 = new Image("img/red_light.png");
		absolutePanel_2.add(image_42, 419, 435);
		image_42.setSize("25px", "25px");
		
		SwitchedLight switchedLight_2 = new SwitchedLight();
		absolutePanel_2.add(switchedLight_2, 133, 180);
		switchedLight_2.setSize("30px", "30px");
		
		SwitchedLight switchedLight_18 = new SwitchedLight();
		absolutePanel_2.add(switchedLight_18, 133, 333);
		switchedLight_18.setSize("30px", "30px");
		
		SwitchedLight switchedLight_19 = new SwitchedLight();
		absolutePanel_2.add(switchedLight_19, 87, 435);
		switchedLight_19.setSize("30px", "30px");
		
		SwitchedLight switchedLight_20 = new SwitchedLight();
		absolutePanel_2.add(switchedLight_20, 116, 570);
		switchedLight_20.setSize("30px", "30px");
		
		SwitchedLight switchedLight_21 = new SwitchedLight();
		absolutePanel_2.add(switchedLight_21, 314, 417);
		switchedLight_21.setSize("30px", "30px");
		
		SwitchedLight switchedLight_22 = new SwitchedLight();
		absolutePanel_2.add(switchedLight_22, 485, 435);
		switchedLight_22.setSize("30px", "30px");
		
		SwitchedLight switchedLight_23 = new SwitchedLight();
		absolutePanel_2.add(switchedLight_23, 485, 180);
		switchedLight_23.setSize("30px", "30px");
		
		SwitchedLight switchedLight_24 = new SwitchedLight();
		absolutePanel_2.add(switchedLight_24, 314, 180);
		switchedLight_24.setSize("30px", "30px");
		
		Thermostat thermo_wasplaats = new Thermostat();
		thermo_wasplaats.setId("13");
		absolutePanel_2.add(thermo_wasplaats, 133, 377);
		thermo_wasplaats.setSize("30px", "30px");
		devices.add(thermo_wasplaats);
		
		Thermostat thermo_entertain = new Thermostat();
		thermo_entertain.setId("14");
		absolutePanel_2.add(thermo_entertain, 340, 278);
		thermo_entertain.setSize("30px", "30px");
		devices.add(thermo_entertain);
		
		AbsolutePanel absolutePanel = new AbsolutePanel();
		tabPanel.add(absolutePanel, "Gelijkvloers");
		absolutePanel.setSize("580px", "707px");
		
		Image background = new Image("img/floor0.png");
		absolutePanel.add(background, 0, 0);
		background.setSize("580px", "");
		
		DimmedLight dimmedLight1 = new DimmedLight();
		absolutePanel.add(dimmedLight1, 419, 93);
		dimmedLight1.setSize("30px", "30px");
		
		Image image_4 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_4, 480, 435);
		image_4.setSize("30px", "30px");
		
		Image image_5 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_5, 373, 455);
		image_5.setSize("30px", "30px");
		
		Image image_6 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_6, 133, 132);
		image_6.setSize("30px", "30px");
		
		Image image_7 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_7, 130, 208);
		image_7.setSize("30px", "30px");
		
		Image image_8 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_8, 50, 339);
		image_8.setSize("30px", "30px");
		
		Image image_10 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_10, 131, 338);
		image_10.setSize("30px", "30px");
		
		Image image_11 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_11, 88, 431);
		image_11.setSize("30px", "30px");
		
		Image image_12 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_12, 116, 577);
		image_12.setSize("30px", "30px");
		
		Image image_14 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_14, 329, 440);
		image_14.setSize("30px", "30px");
		
		Image image_9 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_9, 253, 281);
		image_9.setSize("30px", "30px");
		
		Image image_15 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_15, 372, 367);
		image_15.setSize("30px", "30px");
		
		Image image_16 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_16, 317, 502);
		image_16.setSize("30px", "30px");
		
		Thermostat thermo_living = new Thermostat();
		absolutePanel.add(thermo_living, 343, 320);
		thermo_living.setSize("30px", "30px");
		thermo_living.setId("5");
		devices.add(thermo_living);
		
		DimmedLight dimmedLight = new DimmedLight();
		absolutePanel.add(dimmedLight, 391, 216);
		dimmedLight.setSize("30px", "30px");
		
		SwitchedLight switchedLight = new SwitchedLight();
		absolutePanel.add(switchedLight, 125, 47);
		switchedLight.setSize("30px", "30px");
		
		DimmedLight dimmedLight_1 = new DimmedLight();
		absolutePanel.add(dimmedLight_1, 381, 10);
		dimmedLight_1.setSize("30px", "30px");
		
		TemperatureSensor outsideTemp = new TemperatureSensor();
		absolutePanel.add(outsideTemp, 21, 10);
		//switchedLight_28.setSize("30px", "30px");
		outsideTemp.setId("3");
		devices.add(outsideTemp);
		
		TemperatureSensor livingFloor = new TemperatureSensor();
		absolutePanel.add(livingFloor, 373, 278);
		//temperatureSensor.setSize("36px", "18px");
		livingFloor.setId("4");
		devices.add(livingFloor);
		
		Thermostat thermo_marlies = new Thermostat();
		thermo_marlies.setId("7");
		absolutePanel.add(thermo_marlies, 419, 482);
		thermo_marlies.setSize("30px", "30px");
		devices.add(thermo_marlies);
		
		AbsolutePanel absolutePanel_1 = new AbsolutePanel();
		tabPanel.add(absolutePanel_1, "Boven", false);
		absolutePanel_1.setSize("580px", "707px");
		
		Image image = new Image("img/floor1.png");
		absolutePanel_1.add(image, 0, 0);
		image.setSize("580px", "");
		
		DimmedLight dimmedLight_2 = new DimmedLight();
		absolutePanel_1.add(dimmedLight_2, 204, 300);
		dimmedLight_2.setSize("30px", "30px");
		
		Thermostat thermo_tom = new Thermostat();
		absolutePanel_1.add(thermo_tom, 127, 239);
		thermo_tom.setSize("30px", "30px");
		thermo_tom.setId("6");
		devices.add(thermo_tom);
		
		DimmedLight dimmedLight_3 = new DimmedLight();
		absolutePanel_1.add(dimmedLight_3, 439, 311);
		dimmedLight_3.setSize("30px", "30px");
		
		SwitchedLight switchedLight_1 = new SwitchedLight();
		absolutePanel_1.add(switchedLight_1, 101, 188);
		switchedLight_1.setSize("30px", "30px");
		switchedLight_1.setId("2");
		devices.add(switchedLight_1);
		
		SwitchedLight switchedLight_5 = new SwitchedLight();
		absolutePanel_1.add(switchedLight_5, 446, 199);
		switchedLight_5.setSize("30px", "30px");
		
		final SwitchedLight switchedLight_6 = new SwitchedLight();
		switchedLight_6.setId("1");
		absolutePanel_1.add(switchedLight_6, 209, 151);
		switchedLight_6.setSize("30px", "30px");
		devices.add(switchedLight_6);
		
		SwitchedLight switchedLight_7 = new SwitchedLight();
		absolutePanel_1.add(switchedLight_7, 312, 245);
		switchedLight_7.setSize("30px", "30px");
		
		SwitchedLight switchedLight_8 = new SwitchedLight();
		absolutePanel_1.add(switchedLight_8, 264, 214);
		switchedLight_8.setSize("30px", "30px");
		
		SwitchedLight switchedLight_9 = new SwitchedLight();
		absolutePanel_1.add(switchedLight_9, 380, 401);
		switchedLight_9.setSize("30px", "30px");
		
		SwitchedLight switchedLight_10 = new SwitchedLight();
		absolutePanel_1.add(switchedLight_10, 448, 400);
		switchedLight_10.setSize("30px", "30px");
		
		SwitchedLight switchedLight_11 = new SwitchedLight();
		absolutePanel_1.add(switchedLight_11, 501, 440);
		switchedLight_11.setSize("30px", "30px");
		
		SwitchedLight switchedLight_12 = new SwitchedLight();
		absolutePanel_1.add(switchedLight_12, 101, 347);
		switchedLight_12.setSize("30px", "30px");
		
		SwitchedLight switchedLight_13 = new SwitchedLight();
		absolutePanel_1.add(switchedLight_13, 116, 605);
		switchedLight_13.setSize("30px", "30px");
		
		SwitchedLight switchedLight_14 = new SwitchedLight();
		absolutePanel_1.add(switchedLight_14, 90, 445);
		switchedLight_14.setSize("30px", "30px");
		
		SwitchedLight switchedLight_15 = new SwitchedLight();
		absolutePanel_1.add(switchedLight_15, 90, 496);
		switchedLight_15.setSize("30px", "30px");
		
		Thermostat thermo_master = new Thermostat();
		thermo_master.setId("8");
		absolutePanel_1.add(thermo_master, 358, 256);
		thermo_master.setSize("30px", "30px");
		devices.add(thermo_master);
		
		Thermostat thermo_bad = new Thermostat();
		thermo_bad.setId("12");
		absolutePanel_1.add(thermo_bad, 501, 395);
		thermo_bad.setSize("30px", "30px");
		devices.add(thermo_bad);
		
		Thermostat thermo_kamer2 = new Thermostat();
		thermo_kamer2.setId("10");
		absolutePanel_1.add(thermo_kamer2, 90, 395);
		thermo_kamer2.setSize("30px", "30px");
		devices.add(thermo_kamer2);
		
		Thermostat thermo_kamer1 = new Thermostat();
		thermo_kamer1.setId("9");
		absolutePanel_1.add(thermo_kamer1, 163, 565);
		thermo_kamer1.setSize("30px", "30px");
		devices.add(thermo_kamer1);
		
		Thermostat thermo_douche = new Thermostat();
		thermo_douche.setId("11");
		absolutePanel_1.add(thermo_douche, 115, 469);
		thermo_douche.setSize("30px", "30px");
		devices.add(thermo_douche);
		
		AbsolutePanel absolutePanel_3 = new AbsolutePanel();
		tabPanel.add(absolutePanel_3, "Zolder", false);
		absolutePanel_3.setSize("580px", "707px");
		
		Image image_44 = new Image("img/floor2.png");
		absolutePanel_3.add(image_44, 0, 0);
		image_44.setSize("580px", "");
		
		SwitchedLight switchedLight_3 = new SwitchedLight();
		absolutePanel_3.add(switchedLight_3, 284, 215);
		switchedLight_3.setSize("30px", "30px");
		
		SwitchedLight switchedLight_16 = new SwitchedLight();
		absolutePanel_3.add(switchedLight_16, 469, 302);
		switchedLight_16.setSize("30px", "30px");
		
		SwitchedLight switchedLight_17 = new SwitchedLight();
		absolutePanel_3.add(switchedLight_17, 112, 419);
		switchedLight_17.setSize("30px", "30px");
		
		AbsolutePanel absolutePanel_4 = new AbsolutePanel();
		tabPanel.add(absolutePanel_4, "Garage", false);
		absolutePanel_4.setSize("580px", "707px");
		
		Image image_59 = new Image("img/garage.png");
		absolutePanel_4.add(image_59, 0, 0);
		image_59.setSize("", "");
		
		SwitchedLight switchedLight_4 = new SwitchedLight();
		absolutePanel_4.add(switchedLight_4, 83, 318);
		switchedLight_4.setSize("30px", "30px");
		
		SwitchedLight switchedLight_25 = new SwitchedLight();
		absolutePanel_4.add(switchedLight_25, 237, 318);
		switchedLight_25.setSize("30px", "30px");
		
		SwitchedLight switchedLight_26 = new SwitchedLight();
		absolutePanel_4.add(switchedLight_26, 155, 500);
		switchedLight_26.setSize("30px", "30px");
		
		SwitchedLight switchedLight_27 = new SwitchedLight();
		absolutePanel_4.add(switchedLight_27, 62, 462);
		switchedLight_27.setSize("30px", "30px");
		
		AbsolutePanel absolutePanel_5 = new AbsolutePanel();
		tabPanel.add(absolutePanel_5, "Technical", false);
		absolutePanel_5.setSize("580px", "707px");
		
		Label lblMessage = new Label("Message:");
		absolutePanel_5.add(lblMessage, 10, 10);
		
		final Label label = new Label("...");
		absolutePanel_5.add(label, 73, 10);
		
		tabPanel.selectTab(0);

	
		StringBuffer wsUrl = new StringBuffer(baseUrl); 
				wsUrl.append("ws"); // lastly append the WebSocket endpoint.
				
		String url = new UrlBuilder()
				.setProtocol("wss")
				.setHost(Window.Location.getHostName())
				.setPort(Integer.parseInt(Window.Location.getPort()))
				.setPath(wsUrl.toString())
				.buildString();
		
		WebSocket socket = WebSocket.create(url);
		socket.setOnMessage(new MessageHandler(){
			@Override
			public void onOpen(WebSocket socket) {}

			@Override
			public void onClose(WebSocket socket) {}

			@Override
			public void onError(WebSocket socket) {}

			@Override
			public void onMessage(WebSocket socket, MessageEvent event) {
				//label.setText(event.getData());
			}
		});

		final ServiceClient client = new ServiceClient();
		
		Scheduler scheduler = Scheduler.get();
		scheduler.scheduleFixedPeriod(new RepeatingCommand() {
			@Override
			public boolean execute() {
				for (Device device: devices) {
					client.updateDevice(device, label);
				}
				return true;
			}}, 
			5000); // every 5 seconds. 

		
	}

	private String getBaseUrl() {
		String[] pathArray = Window.Location.getPath().split("/");
		StringBuffer tempUrl = new StringBuffer();
		for ( int i = 0; i < pathArray.length -1 ; i++ ) { // don't include the last part (which is index.html)
			tempUrl.append(pathArray[i]);
			tempUrl.append('/');
		}		
		return tempUrl.toString();
	}
}
