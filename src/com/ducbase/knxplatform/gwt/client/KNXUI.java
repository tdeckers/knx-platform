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
		
		SwitchedLight switchedLight_2 = new SwitchedLight();
		absolutePanel_2.add(switchedLight_2, 133, 180);
		switchedLight_2.setSize("30px", "30px");
		
		SwitchedLight slWasplaats = new SwitchedLight();
		slWasplaats.setId("L0.6");
		absolutePanel_2.add(slWasplaats, 133, 333);
		slWasplaats.setSize("30px", "30px");
		devices.add(slWasplaats);
		
		SwitchedLight slTech = new SwitchedLight();
		slTech.setId("L0.8");
		absolutePanel_2.add(slTech, 87, 435);
		slTech.setSize("30px", "30px");
		devices.add(slTech);
		
		SwitchedLight slKelderGarage = new SwitchedLight();
		slKelderGarage.setId("L0.7");
		absolutePanel_2.add(slKelderGarage, 116, 570);
		slKelderGarage.setSize("30px", "30px");
		devices.add(slKelderGarage);
		
		SwitchedLight slKelderInkom = new SwitchedLight();
		slKelderInkom.setId("L0.5");
		absolutePanel_2.add(slKelderInkom, 314, 417);
		slKelderInkom.setSize("30px", "30px");
		devices.add(slKelderInkom);
		
		SwitchedLight switchedLight_22 = new SwitchedLight();
		absolutePanel_2.add(switchedLight_22, 485, 435);
		switchedLight_22.setSize("30px", "30px");
		
		SwitchedLight slPokerroom = new SwitchedLight();
		slPokerroom.setId("L0.3");
		absolutePanel_2.add(slPokerroom, 485, 180);
		slPokerroom.setSize("30px", "30px");
		devices.add(slPokerroom);
		
		SwitchedLight slEntertain = new SwitchedLight();
		slEntertain.setId("L0.2");
		absolutePanel_2.add(slEntertain, 314, 180);
		slEntertain.setSize("30px", "30px");
		devices.add(slEntertain);
		
		Thermostat thermo_wasplaats = new Thermostat();
		thermo_wasplaats.setId("13");
		absolutePanel_2.add(thermo_wasplaats, 116, 380);
		thermo_wasplaats.setSize("30px", "30px");
		devices.add(thermo_wasplaats);
		
		Thermostat thermo_entertain = new Thermostat();
		thermo_entertain.setId("14");
		absolutePanel_2.add(thermo_entertain, 329, 272);
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
		
		Image image_14 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_14, 329, 440);
		image_14.setSize("30px", "30px");
		
		Image image_9 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_9, 253, 281);
		image_9.setSize("30px", "30px");
		
		Image image_15 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_15, 372, 367);
		image_15.setSize("30px", "30px");
		
		Thermostat thermo_living = new Thermostat();
		absolutePanel.add(thermo_living, 343, 320);
		thermo_living.setSize("30px", "30px");
		thermo_living.setId("5");
		devices.add(thermo_living);
		
		DimmedLight dimmedLight = new DimmedLight();
		absolutePanel.add(dimmedLight, 391, 216);
		dimmedLight.setSize("30px", "30px");
		
		SwitchedLight slTerras = new SwitchedLight();
		slTerras.setId("L1.6");
		absolutePanel.add(slTerras, 125, 47);
		slTerras.setSize("30px", "30px");
		devices.add(slTerras);
		
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
		
		SwitchedLight slBureauMarlies = new SwitchedLight();
		slBureauMarlies.setId("L1.5");
		absolutePanel.add(slBureauMarlies, 485, 431);
		slBureauMarlies.setSize("30px", "30px");
		devices.add(slBureauMarlies);
		
		SwitchedLight slGarage = new SwitchedLight();
		slGarage.setId("L1.7");
		absolutePanel.add(slGarage, 111, 574);
		slGarage.setSize("30px", "30px");
		devices.add(slGarage);
		
		SwitchedLight slToiletGV = new SwitchedLight();
		slToiletGV.setId("L1.8");
		absolutePanel.add(slToiletGV, 372, 456);
		slToiletGV.setSize("30px", "30px");
		devices.add(slToiletGV);
		
		SwitchedLight slKeukenBerging = new SwitchedLight();
		slKeukenBerging.setId("L1.10");
		absolutePanel.add(slKeukenBerging, 90, 431);
		slKeukenBerging.setSize("30px", "30px");
		devices.add(slKeukenBerging);
		
		SwitchedLight slVoordeur = new SwitchedLight();
		slVoordeur.setId("L1.13");
		absolutePanel.add(slVoordeur, 316, 502);
		slVoordeur.setSize("30px", "30px");
		devices.add(slVoordeur);
		
		SwitchedLight slKeukenRamen = new SwitchedLight();
		slKeukenRamen.setId("L1.14");
		absolutePanel.add(slKeukenRamen, 130, 131);
		slKeukenRamen.setSize("30px", "30px");
		devices.add(slKeukenRamen);
		
		SwitchedLight slKeukenTafel = new SwitchedLight();
		slKeukenTafel.setId("L1.15");
		absolutePanel.add(slKeukenTafel, 128, 220);
		slKeukenTafel.setSize("30px", "30px");
		devices.add(slKeukenTafel);
		
		SwitchedLight slDampkap = new SwitchedLight();
		slDampkap.setId("L1.16");
		absolutePanel.add(slDampkap, 129, 339);
		slDampkap.setSize("30px", "30px");
		devices.add(slDampkap);
		
		SwitchedLight slWasbak = new SwitchedLight();
		slWasbak.setId("L1.17");
		absolutePanel.add(slWasbak, 53, 339);
		slWasbak.setSize("30px", "30px");
		devices.add(slWasbak);
		
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
		absolutePanel_1.add(thermo_tom, 116, 237);
		thermo_tom.setSize("30px", "30px");
		thermo_tom.setId("6");
		devices.add(thermo_tom);
		
		DimmedLight dimmedLight_3 = new DimmedLight();
		absolutePanel_1.add(dimmedLight_3, 439, 311);
		dimmedLight_3.setSize("30px", "30px");
		
		SwitchedLight slBureauTom = new SwitchedLight();
		absolutePanel_1.add(slBureauTom, 101, 188);
		slBureauTom.setSize("30px", "30px");
		slBureauTom.setId("L2.1");
		devices.add(slBureauTom);
		
		SwitchedLight slMasterCentraal = new SwitchedLight();
		slMasterCentraal.setId("L2.8");
		absolutePanel_1.add(slMasterCentraal, 446, 199);
		slMasterCentraal.setSize("30px", "30px");
		devices.add(slMasterCentraal);
		
		SwitchedLight slBureauTomVoor = new SwitchedLight();
		slBureauTomVoor.setId("L2.2");
		absolutePanel_1.add(slBureauTomVoor, 209, 151);
		slBureauTomVoor.setSize("30px", "30px");
		devices.add(slBureauTomVoor);
		
		SwitchedLight slToiletVerdiep = new SwitchedLight();
		slToiletVerdiep.setId("L2.12");
		absolutePanel_1.add(slToiletVerdiep, 312, 245);
		slToiletVerdiep.setSize("30px", "30px");
		devices.add(slToiletVerdiep);
		
		SwitchedLight slZolderTrap = new SwitchedLight();
		slZolderTrap.setId("L3.1");
		absolutePanel_1.add(slZolderTrap, 264, 214);
		slZolderTrap.setSize("30px", "30px");
		devices.add(slZolderTrap);
		
		SwitchedLight slDressing = new SwitchedLight();
		slDressing.setId("L2.5");
		absolutePanel_1.add(slDressing, 380, 401);
		slDressing.setSize("30px", "30px");
		devices.add(slDressing);
		
		SwitchedLight slBadWaskommen = new SwitchedLight();
		slBadWaskommen.setId("L2.7");
		absolutePanel_1.add(slBadWaskommen, 448, 400);
		slBadWaskommen.setSize("30px", "30px");
		devices.add(slBadWaskommen);
		
		SwitchedLight slBadCentraal = new SwitchedLight();
		slBadCentraal.setId("L2.6");
		absolutePanel_1.add(slBadCentraal, 501, 440);
		slBadCentraal.setSize("30px", "30px");
		devices.add(slBadCentraal);
		
		SwitchedLight slKamer2 = new SwitchedLight();
		slKamer2.setId("L2.10");
		absolutePanel_1.add(slKamer2, 101, 347);
		slKamer2.setSize("30px", "30px");
		devices.add(slKamer2);
		
		SwitchedLight slKamer1 = new SwitchedLight();
		slKamer1.setId("L2.13");
		absolutePanel_1.add(slKamer1, 116, 605);
		slKamer1.setSize("30px", "30px");
		devices.add(slKamer1);
		
		SwitchedLight slDoucheWaskommen = new SwitchedLight();
		slDoucheWaskommen.setId("L2.11");
		absolutePanel_1.add(slDoucheWaskommen, 90, 445);
		slDoucheWaskommen.setSize("30px", "30px");
		devices.add(slDoucheWaskommen);
		
		SwitchedLight slDoucheCentraal = new SwitchedLight();
		slDoucheCentraal.setId("L2.14");
		absolutePanel_1.add(slDoucheCentraal, 71, 502);
		slDoucheCentraal.setSize("30px", "30px");
		devices.add(slDoucheCentraal);
		
		Thermostat thermo_master = new Thermostat();
		thermo_master.setId("8");
		absolutePanel_1.add(thermo_master, 358, 256);
		thermo_master.setSize("30px", "30px");
		devices.add(thermo_master);
		
		Thermostat thermo_bad = new Thermostat();
		thermo_bad.setId("12");
		absolutePanel_1.add(thermo_bad, 491, 395);
		thermo_bad.setSize("30px", "30px");
		devices.add(thermo_bad);
		
		Thermostat thermo_kamer2 = new Thermostat();
		thermo_kamer2.setId("10");
		absolutePanel_1.add(thermo_kamer2, 81, 395);
		thermo_kamer2.setSize("30px", "30px");
		devices.add(thermo_kamer2);
		
		Thermostat thermo_kamer1 = new Thermostat();
		thermo_kamer1.setId("9");
		absolutePanel_1.add(thermo_kamer1, 163, 565);
		thermo_kamer1.setSize("30px", "30px");
		devices.add(thermo_kamer1);
		
		Thermostat thermo_douche = new Thermostat();
		thermo_douche.setId("11");
		absolutePanel_1.add(thermo_douche, 109, 471);
		thermo_douche.setSize("30px", "30px");
		devices.add(thermo_douche);
		
		SwitchedLight slOverloopCentraal = new SwitchedLight();
		slOverloopCentraal.setId("L2.4");
		absolutePanel_1.add(slOverloopCentraal, 286, 386);
		slOverloopCentraal.setSize("30px", "30px");
		devices.add(slOverloopCentraal);
		
		AbsolutePanel absolutePanel_3 = new AbsolutePanel();
		tabPanel.add(absolutePanel_3, "Zolder", false);
		absolutePanel_3.setSize("580px", "707px");		
		
		Image image_44 = new Image("img/floor2.png");
		absolutePanel_3.add(image_44, 0, 0);
		image_44.setSize("580px", "");
		
		SwitchedLight slZolderTrap2 = new SwitchedLight();
		slZolderTrap2.setId("L3.1");
		absolutePanel_3.add(slZolderTrap2, 284, 215);
		slZolderTrap2.setSize("30px", "30px");
		devices.add(slZolderTrap2);
		
		SwitchedLight slZolderCentraal = new SwitchedLight();
		slZolderCentraal.setId("L3.2");
		absolutePanel_3.add(slZolderCentraal, 469, 302);
		slZolderCentraal.setSize("30px", "30px");
		devices.add(slZolderCentraal);
		
		SwitchedLight slZolderCentraal2 = new SwitchedLight();
		slZolderCentraal2.setId("L3.2");
		absolutePanel_3.add(slZolderCentraal2, 112, 419);
		slZolderCentraal2.setSize("30px", "30px");
		devices.add(slZolderCentraal2);
		
		AbsolutePanel absolutePanel_4 = new AbsolutePanel();
		tabPanel.add(absolutePanel_4, "Garage", false);
		absolutePanel_4.setSize("580px", "707px");
		
		Image image_59 = new Image("img/garage.png");
		absolutePanel_4.add(image_59, 0, 0);
		image_59.setSize("", "");
		
		SwitchedLight slWerkplaats = new SwitchedLight();
		slWerkplaats.setId("Lg.1");
		absolutePanel_4.add(slWerkplaats, 83, 318);
		slWerkplaats.setSize("30px", "30px");
		devices.add(slWerkplaats);
		
		SwitchedLight slTuingarage = new SwitchedLight();
		slTuingarage.setId("Lg.2");
		absolutePanel_4.add(slTuingarage, 237, 318);
		slTuingarage.setSize("30px", "30px");
		devices.add(slTuingarage);
		
		SwitchedLight slToiletTuin = new SwitchedLight();
		slToiletTuin.setId("Lg.3");
		absolutePanel_4.add(slToiletTuin, 155, 500);
		slToiletTuin.setSize("30px", "30px");
		devices.add(slToiletTuin);
		
		SwitchedLight slTuinBuiten = new SwitchedLight();
		slTuinBuiten.setId("Lg.4");
		absolutePanel_4.add(slTuinBuiten, 62, 462);
		slTuinBuiten.setSize("30px", "30px");
		devices.add(slTuinBuiten);
		
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
