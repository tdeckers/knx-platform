package com.ducbase.knxplatform.gwt.client;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.ducbase.knxplatform.gwt.client.ws.MessageEvent;
import com.ducbase.knxplatform.gwt.client.ws.MessageHandler;
import com.ducbase.knxplatform.gwt.client.ws.WebSocket;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.CheckBox;

public class KNXUI implements EntryPoint {

	private String baseServerUrl;
	private String webSocketUrl;

	TextBox tbServerUrl = new TextBox();
	Label lblWebSocketUrl = new Label("");
	
	public String getBaseServerUrl() {
		return baseServerUrl;
	}

	public void setBaseServerUrl(String baseServerUrl) {
		this.baseServerUrl = baseServerUrl;
		tbServerUrl.setText(baseServerUrl);
				
		webSocketUrl = this.baseServerUrl + "ws";
		webSocketUrl = webSocketUrl.replaceAll("https", "wss");
		lblWebSocketUrl.setText(webSocketUrl);
		
		Device.setBaseUrl(baseServerUrl); // TODO: revisit this static config mess.
	}
	
	private Map<String, Device> devices = new HashMap<String, Device>();
	
	@Override
	public void onModuleLoad() {

		// Configure base Url of the application.  Use to configure devices.
		// TODO: make this configurable if REST API is not with UI app.
		this.setBaseServerUrl(generateBaseUrl());

		
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
		devices.put(slWasplaats.getId(), slWasplaats);
		
		SwitchedLight slTech = new SwitchedLight();
		slTech.setId("L0.8");
		absolutePanel_2.add(slTech, 87, 435);
		slTech.setSize("30px", "30px");
		devices.put(slTech.getId(), slTech);
		
		SwitchedLight slKelderGarage = new SwitchedLight();
		slKelderGarage.setId("L0.7");
		absolutePanel_2.add(slKelderGarage, 116, 570);
		slKelderGarage.setSize("30px", "30px");
		devices.put(slKelderGarage.getId(), slKelderGarage);
		
		SwitchedLight slKelderInkom = new SwitchedLight();
		slKelderInkom.setId("L0.5");
		absolutePanel_2.add(slKelderInkom, 314, 417);
		slKelderInkom.setSize("30px", "30px");
		devices.put(slKelderInkom.getId(), slKelderInkom);
		
		SwitchedLight switchedLight_22 = new SwitchedLight();
		absolutePanel_2.add(switchedLight_22, 485, 435);
		switchedLight_22.setSize("30px", "30px");
		
		SwitchedLight slPokerroom = new SwitchedLight();
		slPokerroom.setId("L0.3");
		absolutePanel_2.add(slPokerroom, 485, 180);
		slPokerroom.setSize("30px", "30px");
		devices.put(slPokerroom.getId(), slPokerroom);
		
		SwitchedLight slEntertain = new SwitchedLight();
		slEntertain.setId("L0.2");
		absolutePanel_2.add(slEntertain, 314, 180);
		slEntertain.setSize("30px", "30px");
		devices.put(slEntertain.getId(), slEntertain);
		
		Thermostat thermo_wasplaats = new Thermostat();
		thermo_wasplaats.setId("13");
		absolutePanel_2.add(thermo_wasplaats, 116, 380);
		thermo_wasplaats.setSize("30px", "30px");
		devices.put(thermo_wasplaats.getId(), thermo_wasplaats);
		
		Thermostat thermo_entertain = new Thermostat();
		thermo_entertain.setId("14");
		absolutePanel_2.add(thermo_entertain, 329, 272);
		thermo_entertain.setSize("30px", "30px");
		devices.put(thermo_entertain.getId(), thermo_entertain);
		
		AbsolutePanel absolutePanel = new AbsolutePanel();
		tabPanel.add(absolutePanel, "Gelijkvloers");
		absolutePanel.setSize("580px", "707px");
		
		Image background = new Image("img/floor0.png");
		absolutePanel.add(background, 0, 0);
		background.setSize("580px", "");
		
		DimmedLight dlLivingTafel = new DimmedLight();
		dlLivingTafel.setId("L1.2");
		absolutePanel.add(dlLivingTafel, 419, 93);
		dlLivingTafel.setSize("30px", "30px");
		devices.put(dlLivingTafel.getId(), dlLivingTafel);
		
		Image image_14 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_14, 329, 440);
		image_14.setSize("30px", "30px");
		
		final Image image_9 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_9, 253, 281);
		image_9.setSize("30px", "30px");
		image_9.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				shakeWidget(image_9);				
			}
		});
		
		Image image_15 = new Image("img/light_bulb_off.png");
		absolutePanel.add(image_15, 372, 367);
		image_15.setSize("30px", "30px");
		
		Thermostat thermo_living = new Thermostat();
		absolutePanel.add(thermo_living, 343, 320);
		thermo_living.setSize("30px", "30px");
		thermo_living.setId("5");
		devices.put(thermo_living.getId(), thermo_living);
		
		DimmedLight dlLivingSalon = new DimmedLight();
		dlLivingSalon.setId("L1.3");
		absolutePanel.add(dlLivingSalon, 391, 216);
		dlLivingSalon.setSize("30px", "30px");
		devices.put(dlLivingSalon.getId(), dlLivingSalon);
		
		SwitchedLight slTerras = new SwitchedLight();
		slTerras.setId("L1.6");
		absolutePanel.add(slTerras, 125, 47);
		slTerras.setSize("30px", "30px");
		devices.put(slTerras.getId(), slTerras);
		
		DimmedLight dlLivingRamen = new DimmedLight();
		dlLivingRamen.setId("L1.1");
		absolutePanel.add(dlLivingRamen, 381, 10);
		dlLivingRamen.setSize("30px", "30px");
		devices.put(dlLivingRamen.getId(), dlLivingRamen);
		
		TemperatureSensor outsideTemp = new TemperatureSensor();
		absolutePanel.add(outsideTemp, 21, 10);
		//switchedLight_28.setSize("30px", "30px");
		outsideTemp.setId("3");
		devices.put(outsideTemp.getId(), outsideTemp);
		
		TemperatureSensor livingFloor = new TemperatureSensor();
		absolutePanel.add(livingFloor, 373, 278);
		//temperatureSensor.setSize("36px", "18px");
		livingFloor.setId("4");
		devices.put(livingFloor.getId(), livingFloor);
		
		Thermostat thermo_marlies = new Thermostat();
		thermo_marlies.setId("7");
		absolutePanel.add(thermo_marlies, 419, 482);
		thermo_marlies.setSize("30px", "30px");
		devices.put(thermo_marlies.getId(), thermo_marlies);
		
		SwitchedLight slBureauMarlies = new SwitchedLight();
		slBureauMarlies.setId("L1.5");
		absolutePanel.add(slBureauMarlies, 485, 431);
		slBureauMarlies.setSize("30px", "30px");
		devices.put(slBureauMarlies.getId(), slBureauMarlies);
		
		SwitchedLight slGarage = new SwitchedLight();
		slGarage.setId("L1.7");
		absolutePanel.add(slGarage, 111, 574);
		slGarage.setSize("30px", "30px");
		devices.put(slGarage.getId(), slGarage);
		
		SwitchedLight slToiletGV = new SwitchedLight();
		slToiletGV.setId("L1.8");
		absolutePanel.add(slToiletGV, 372, 456);
		slToiletGV.setSize("30px", "30px");
		devices.put(slToiletGV.getId(), slToiletGV);
		
		SwitchedLight slKeukenBerging = new SwitchedLight();
		slKeukenBerging.setId("L1.10");
		absolutePanel.add(slKeukenBerging, 90, 431);
		slKeukenBerging.setSize("30px", "30px");
		devices.put(slKeukenBerging.getId(), slKeukenBerging);
		
		SwitchedLight slVoordeur = new SwitchedLight();
		slVoordeur.setId("L1.13");
		absolutePanel.add(slVoordeur, 316, 502);
		slVoordeur.setSize("30px", "30px");
		devices.put(slVoordeur.getId(), slVoordeur);
		
		SwitchedLight slKeukenRamen = new SwitchedLight();
		slKeukenRamen.setId("L1.14");
		absolutePanel.add(slKeukenRamen, 130, 131);
		slKeukenRamen.setSize("30px", "30px");
		devices.put(slKeukenRamen.getId(), slKeukenRamen);
		
		SwitchedLight slKeukenTafel = new SwitchedLight();
		slKeukenTafel.setId("L1.15");
		absolutePanel.add(slKeukenTafel, 128, 220);
		slKeukenTafel.setSize("30px", "30px");
		devices.put(slKeukenTafel.getId(), slKeukenTafel);
		
		SwitchedLight slDampkap = new SwitchedLight();
		slDampkap.setId("L1.16");
		absolutePanel.add(slDampkap, 129, 339);
		slDampkap.setSize("30px", "30px");
		devices.put(slDampkap.getId(), slDampkap);
		
		SwitchedLight slWasbak = new SwitchedLight();
		slWasbak.setId("L1.17");
		absolutePanel.add(slWasbak, 53, 339);
		slWasbak.setSize("30px", "30px");
		devices.put(slWasbak.getId(), slWasbak);
		
		BooleanCommand gateCommand = new BooleanCommand();
		gateCommand.setCode("6969");
		gateCommand.setId("B7");
		absolutePanel.add(gateCommand, 241, 593);
		gateCommand.setSize("30px", "30px");
		devices.put(gateCommand.getId(), gateCommand);
		
		AbsolutePanel absolutePanel_1 = new AbsolutePanel();
		tabPanel.add(absolutePanel_1, "Boven", false);
		absolutePanel_1.setSize("580px", "707px");
		
		Image image = new Image("img/floor1.png");
		absolutePanel_1.add(image, 0, 0);
		image.setSize("580px", "");
		
		DimmedLight dlOverloop = new DimmedLight();
		dlOverloop.setId("L2.3");
		absolutePanel_1.add(dlOverloop, 204, 300);
		dlOverloop.setSize("30px", "30px");
		devices.put(dlOverloop.getId(), dlOverloop);
		
		Thermostat thermo_tom = new Thermostat();
		absolutePanel_1.add(thermo_tom, 116, 237);
		thermo_tom.setSize("30px", "30px");
		thermo_tom.setId("6");
		devices.put(thermo_tom.getId(), thermo_tom);
		
		DimmedLight dlMasterGang = new DimmedLight();
		dlMasterGang.setId("L2.9");
		absolutePanel_1.add(dlMasterGang, 439, 311);
		dlMasterGang.setSize("30px", "30px");
		devices.put(dlMasterGang.getId(), dlMasterGang);
		
		SwitchedLight slBureauTom = new SwitchedLight();
		absolutePanel_1.add(slBureauTom, 101, 188);
		slBureauTom.setSize("30px", "30px");
		slBureauTom.setId("L2.1");
		devices.put(slBureauTom.getId(), slBureauTom);
		
		SwitchedLight slMasterCentraal = new SwitchedLight();
		slMasterCentraal.setId("L2.8");
		absolutePanel_1.add(slMasterCentraal, 446, 199);
		slMasterCentraal.setSize("30px", "30px");
		devices.put(slMasterCentraal.getId(), slMasterCentraal);
		
		SwitchedLight slBureauTomVoor = new SwitchedLight();
		slBureauTomVoor.setId("L2.2");
		absolutePanel_1.add(slBureauTomVoor, 209, 151);
		slBureauTomVoor.setSize("30px", "30px");
		devices.put(slBureauTomVoor.getId(), slBureauTomVoor);
		
		SwitchedLight slToiletVerdiep = new SwitchedLight();
		slToiletVerdiep.setId("L2.12");
		absolutePanel_1.add(slToiletVerdiep, 312, 245);
		slToiletVerdiep.setSize("30px", "30px");
		devices.put(slToiletVerdiep.getId(), slToiletVerdiep);
		
		SwitchedLight slZolderTrap = new SwitchedLight();
		slZolderTrap.setId("L3.1");
		absolutePanel_1.add(slZolderTrap, 264, 214);
		slZolderTrap.setSize("30px", "30px");
		devices.put(slZolderTrap.getId(), slZolderTrap);
		
		SwitchedLight slDressing = new SwitchedLight();
		slDressing.setId("L2.5");
		absolutePanel_1.add(slDressing, 380, 401);
		slDressing.setSize("30px", "30px");
		devices.put(slDressing.getId(), slDressing);
		
		SwitchedLight slBadWaskommen = new SwitchedLight();
		slBadWaskommen.setId("L2.7");
		absolutePanel_1.add(slBadWaskommen, 448, 400);
		slBadWaskommen.setSize("30px", "30px");
		devices.put(slBadWaskommen.getId(), slBadWaskommen);
		
		SwitchedLight slBadCentraal = new SwitchedLight();
		slBadCentraal.setId("L2.6");
		absolutePanel_1.add(slBadCentraal, 501, 440);
		slBadCentraal.setSize("30px", "30px");
		devices.put(slBadCentraal.getId(), slBadCentraal);
		
		SwitchedLight slKamer2 = new SwitchedLight();
		slKamer2.setId("L2.10");
		absolutePanel_1.add(slKamer2, 101, 347);
		slKamer2.setSize("30px", "30px");
		devices.put(slKamer2.getId(), slKamer2);
		
		SwitchedLight slKamer1 = new SwitchedLight();
		slKamer1.setId("L2.13");
		absolutePanel_1.add(slKamer1, 116, 605);
		slKamer1.setSize("30px", "30px");
		devices.put(slKamer1.getId(), slKamer1);
		
		SwitchedLight slDoucheWaskommen = new SwitchedLight();
		slDoucheWaskommen.setId("L2.11");
		absolutePanel_1.add(slDoucheWaskommen, 90, 445);
		slDoucheWaskommen.setSize("30px", "30px");
		devices.put(slDoucheWaskommen.getId(), slDoucheWaskommen);
		
		SwitchedLight slDoucheCentraal = new SwitchedLight();
		slDoucheCentraal.setId("L2.14");
		absolutePanel_1.add(slDoucheCentraal, 71, 502);
		slDoucheCentraal.setSize("30px", "30px");
		devices.put(slDoucheCentraal.getId(), slDoucheCentraal);
		
		Thermostat thermo_master = new Thermostat();
		thermo_master.setId("8");
		absolutePanel_1.add(thermo_master, 358, 256);
		thermo_master.setSize("30px", "30px");
		devices.put(thermo_master.getId(), thermo_master);
		
		Thermostat thermo_bad = new Thermostat();
		thermo_bad.setId("12");
		absolutePanel_1.add(thermo_bad, 491, 395);
		thermo_bad.setSize("30px", "30px");
		devices.put(thermo_bad.getId(), thermo_bad);
		
		Thermostat thermo_kamer2 = new Thermostat();
		thermo_kamer2.setId("10");
		absolutePanel_1.add(thermo_kamer2, 81, 395);
		thermo_kamer2.setSize("30px", "30px");
		devices.put(thermo_kamer2.getId(), thermo_kamer2);
		
		Thermostat thermo_kamer1 = new Thermostat();
		thermo_kamer1.setId("9");
		absolutePanel_1.add(thermo_kamer1, 163, 565);
		thermo_kamer1.setSize("30px", "30px");
		devices.put(thermo_kamer1.getId(), thermo_kamer1);
		
		Thermostat thermo_douche = new Thermostat();
		thermo_douche.setId("11");
		absolutePanel_1.add(thermo_douche, 109, 471);
		thermo_douche.setSize("30px", "30px");
		devices.put(thermo_douche.getId(), thermo_douche);
		
		SwitchedLight slOverloopCentraal = new SwitchedLight();
		slOverloopCentraal.setId("L2.4");
		absolutePanel_1.add(slOverloopCentraal, 286, 386);
		slOverloopCentraal.setSize("30px", "30px");
		devices.put(slOverloopCentraal.getId(), slOverloopCentraal);
		
		Shutter shKamer2 = new Shutter();
		shKamer2.setId("SH3");
		absolutePanel_1.add(shKamer2, 15, 344);
		shKamer2.setSize("40px", "40px");
		devices.put(shKamer2.getId(), shKamer2);
		
		Shutter shMaster = new Shutter();
		shMaster.setId("SH1");
		absolutePanel_1.add(shMaster, 452, 91);
		shMaster.setSize("40px", "40px");
		devices.put(shMaster.getId(), shMaster);
		
		Shutter shKamer1 = new Shutter();
		shKamer1.setId("SH2");
		absolutePanel_1.add(shKamer1, 123, 659);
		shKamer1.setSize("40px", "40px");
		devices.put(shKamer1.getId(), shKamer1);
		
		
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
		devices.put(slZolderTrap2.getId(), slZolderTrap2);
		
		SwitchedLight slZolderCentraal = new SwitchedLight();
		slZolderCentraal.setId("L3.2");
		absolutePanel_3.add(slZolderCentraal, 469, 302);
		slZolderCentraal.setSize("30px", "30px");
		devices.put(slZolderCentraal.getId(), slZolderCentraal);
		
		SwitchedLight slZolderCentraal2 = new SwitchedLight();
		slZolderCentraal2.setId("L3.2");
		absolutePanel_3.add(slZolderCentraal2, 112, 419);
		slZolderCentraal2.setSize("30px", "30px");
		devices.put(slZolderCentraal2.getId(), slZolderCentraal2);
		
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
		devices.put(slWerkplaats.getId(), slWerkplaats);
		
		SwitchedLight slTuingarage = new SwitchedLight();
		slTuingarage.setId("Lg.2");
		absolutePanel_4.add(slTuingarage, 237, 318);
		slTuingarage.setSize("30px", "30px");
		devices.put(slTuingarage.getId(), slTuingarage);
		
		SwitchedLight slToiletTuin = new SwitchedLight();
		slToiletTuin.setId("Lg.3");
		absolutePanel_4.add(slToiletTuin, 155, 500);
		slToiletTuin.setSize("30px", "30px");
		devices.put(slToiletTuin.getId(), slToiletTuin);
		
		SwitchedLight slTuinBuiten = new SwitchedLight();
		slTuinBuiten.setId("Lg.4");
		absolutePanel_4.add(slTuinBuiten, 62, 462);
		slTuinBuiten.setSize("30px", "30px");
		devices.put(slTuinBuiten.getId(), slTuinBuiten);
		
		AbsolutePanel absolutePanel_5 = new AbsolutePanel();
		tabPanel.add(absolutePanel_5, "Technical", false);
		absolutePanel_5.setSize("580px", "707px");
		
		Label lblMessage = new Label("Message:");
		absolutePanel_5.add(lblMessage, 10, 10);
		
		final Label msgLabel = new Label("...");
		absolutePanel_5.add(msgLabel, 73, 10);
		
		BooleanStatus blHeating = new BooleanStatus();
		blHeating.setId("B1");
		absolutePanel_5.add(blHeating, 152, 174);
		blHeating.setSize("46px", "18px");
		devices.put(blHeating.getId(), blHeating);
		
		Label lblHeating = new Label("Heating:");
		absolutePanel_5.add(lblHeating, 10, 174);
		lblHeating.setSize("123px", "18px");
		
		Label lblAlarmvolledig = new Label("Alarm (volledig):");
		absolutePanel_5.add(lblAlarmvolledig, 10, 89);
		lblAlarmvolledig.setSize("123px", "18px");
		
		Label lblAlarmgedeeltelijk = new Label("Alarm (gedeeltelijk):");
		absolutePanel_5.add(lblAlarmgedeeltelijk, 10, 113);
		lblAlarmgedeeltelijk.setSize("123px", "18px");
		
		BooleanStatus blAlarmVoll = new BooleanStatus();
		blAlarmVoll.setId("B2");
		absolutePanel_5.add(blAlarmVoll, 153, 89);
		blAlarmVoll.setSize("46px", "18px");
		devices.put(blAlarmVoll.getId(), blAlarmVoll);
		
		BooleanStatus blAlarmGed = new BooleanStatus();
		blAlarmGed.setId("B3");
		absolutePanel_5.add(blAlarmGed, 153, 113);
		blAlarmGed.setSize("46px", "18px");
		devices.put(blAlarmGed.getId(), blAlarmGed);
		
		Label lblWebsocketStatus = new Label("WebSocket status:");
		absolutePanel_5.add(lblWebsocketStatus, 11, 34);
		
		final Label wsStatusLabel = new Label("...");
		absolutePanel_5.add(wsStatusLabel, 143, 34);
		
		Label lblPumpVerd = new Label("Pomp verdieping:");
		absolutePanel_5.add(lblPumpVerd, 10, 198);
		lblPumpVerd.setSize("123px", "18px");
		
		BooleanStatus blPumpVerd = new BooleanStatus();
		blPumpVerd.setId("B5");
		absolutePanel_5.add(blPumpVerd, 152, 198);
		blPumpVerd.setSize("46px", "18px");
		devices.put(blPumpVerd.getId(), blPumpVerd);
		
		Label lblDisplays = new Label("LED displays:");
		absolutePanel_5.add(lblDisplays, 315, 89);
		lblDisplays.setSize("123px", "18px");
		
		SwitchedLight ledLight = new SwitchedLight();
		ledLight.setId("B6");
		absolutePanel_5.add(ledLight, 404, 84);
		ledLight.setSize("30px", "30px");
		devices.put(ledLight.getId(), ledLight);
		
		Label lblServerUrl = new Label("ServerUrl:");
		absolutePanel_5.add(lblServerUrl, 10, 346);
		lblServerUrl.setSize("88px", "18px");
		
		// Server Url text box (initialized at the top!)
		absolutePanel_5.add(tbServerUrl, 105, 343);
		tbServerUrl.setSize("303px", "16px");
		
		Label lblWebsocketurl = new Label("WebSocketUrl:");
		absolutePanel_5.add(lblWebsocketurl, 10, 376);
		lblWebsocketurl.setSize("88px", "18px");
		
		// Web Socket URL label (initialized at the top)
		absolutePanel_5.add(lblWebSocketUrl, 104, 376);
		lblWebSocketUrl.setSize("312px", "18px");
		
		Button btnServerUrl = new Button("Update");
		absolutePanel_5.add(btnServerUrl, 420, 343);
		
		final CheckBox chkAnimations = new CheckBox("New check box");
		chkAnimations.setHTML("Animations");
		absolutePanel_5.add(chkAnimations, 10, 277);
		btnServerUrl.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setBaseServerUrl(tbServerUrl.getText());				
			}
		});
		
		tabPanel.selectTab(1); // set ground floor as default.

		WebSocket socket = WebSocket.create(webSocketUrl);
		MessageHandler handler = new MessageHandler(){
			DateTimeFormat fmt = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss [zzz]");
			int connectCount = 0;
			
			@Override
			public void onOpen(WebSocket socket) {
				wsStatusLabel.setText("Opened (" + fmt.format(new Date()) + ", count: " + ++connectCount + ").");
			}

			@Override
			public void onClose(WebSocket socket) {
				wsStatusLabel.setText("Closed, recreating...");

				socket = WebSocket.create(webSocketUrl);
				socket.setOnMessage(this);
				socket.setOnClose(this);
				socket.setOnError(this);
				socket.setOnOpen(this);
			}

			@Override
			public void onError(WebSocket socket) {
				wsStatusLabel.setText("Error (" + fmt.format(new Date()) + ").");
			}

			@Override
			public void onMessage(WebSocket socket, MessageEvent event) {
				String data = event.getData();
				try {
					DeviceVO jso = JsonUtils.safeEval(data);
					String id = jso.getId();
					Device device = devices.get(id);
					device.update(data);
					msgLabel.setText(fmt.format(new Date()) + " - Updated device " + id);
					// let's add some movement magic!
					if (chkAnimations.getValue()) {
						shakeWidget(device.asWidget());
					}
				} catch (Exception e) {
					msgLabel.setText("WS EXCEPTION: " + e.getMessage());
				}				
			}
		};
		
		socket.setOnMessage(handler);
		socket.setOnClose(handler);
		socket.setOnError(handler);
		socket.setOnOpen(handler);


		
		// Update all devices to initialize values.
		final ServiceClient client = new ServiceClient();
		for (Device device: devices.values()) {
			client.updateDevice(device, msgLabel);
		}

		// And now do this every so many minutes, in case WebSockets update
		// misses one.
		Scheduler scheduler = Scheduler.get();
		scheduler.scheduleFixedPeriod(new RepeatingCommand() {
			@Override
			public boolean execute() {
				// Renew all devices status
				for (Device device: devices.values()) {
					client.updateDevice(device, msgLabel);
				}
				return true;
			}}, 
			5 * 60 * 1000); // every 5 minutes. 			
	}

	private String generateBaseUrl() {
		String[] pathArray = Window.Location.getPath().split("/");
		StringBuffer newUrl = new StringBuffer();
		newUrl.append(Window.Location.getProtocol()).append("//");
		newUrl.append(Window.Location.getHostName()).append(':');
		newUrl.append(Window.Location.getPort());
		
		for ( int i = 0; i < pathArray.length -1 ; i++ ) { // don't include the last part (which is index.html)
			newUrl.append(pathArray[i]);
			newUrl.append('/');
		}				
		return newUrl.toString();
	}
	
	HashSet<Widget> shaking = new HashSet<Widget>(); // keep track of who's shaking.  Can't double shake!
	
	private void shakeWidget(Widget widget) {
		if (!shaking.contains(widget)) {  // only shake if we're not shaking.
			ShakeAnimation animation = new ShakeAnimation(widget);
			animation.run(400);
		}
	}
	
	class ShakeAnimation extends Animation {
		AbsolutePanel panel;
		Widget widget;
		int initialX;
		int initialY;
		int factor = 15;
		
		ShakeAnimation(Widget w) {
			Widget parent =  w.getParent();
			if (parent instanceof AbsolutePanel) {
				panel = (AbsolutePanel) parent;
				widget = w;				
				initialX = panel.getWidgetLeft(w);
				initialY = panel.getWidgetTop(w);
			}			
		}
		
		protected void onStart() {
			shaking.add(widget);
		}
		
		protected void onComplete() {
			shaking.remove(widget);
			panel.setWidgetPosition(widget, initialX, initialY);  // make sure we're back to where we were.
		}
		
		protected void onCancel() {
			shaking.remove(widget);
			panel.setWidgetPosition(widget, initialX, initialY); // make sure we're back to where we were.
		}
		
		@Override
		protected void onUpdate(double progress) {
			int offsetX = Math.round((float) Math.sin(Math.PI * progress * 4) * factor);
			int offsetY = Math.round((float) Math.sin(Math.PI * progress * 2) * factor);
			panel.setWidgetPosition(widget, initialX + offsetX, initialY - offsetY);			
		}
		
	}
	

	static public class DeviceVO extends JavaScriptObject {
		
		// Overlay types always have protected, zero argument constructors.
		  protected DeviceVO() {}
		  
		// JSNI methods to get stock data.
		  public final native String getId() /*-{ return this.id; }-*/;  
	}	
}
