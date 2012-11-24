package com.ducbase.knxplatform.gwt.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Label;

public class ServiceClient {
	
	public ServiceClient() {		
	}
	
	
	public void setLight(boolean on, final Label label) {
		 RequestBuilder builder = new RequestBuilder(RequestBuilder.PUT, "");
		 builder.setHeader("Content-Type", "application/json");
		 String data = "{'on': '" + on + "'}";
		 try {
			Request request = builder.sendRequest(data, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					if (200 == response.getStatusCode()) {
						//
					} else {
						label.setText("" + response.getStatusCode());
			        }
					
				}

				@Override
				public void onError(Request request, Throwable exception) {
					label.setText(exception.getMessage());
					
				}});
		} catch (RequestException e) {
			// TODO Auto-generated catch block
		}
	}

	public void updateDevice(final Device device, Label label) {
		String url = device.getUrl();

		 RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		 builder.setHeader("Accept", "application/json");

		    try {
		      Request request = builder.sendRequest(null, new RequestCallback() {
		        public void onError(Request request, Throwable exception) {
		         // displayError("Couldn't retrieve JSON");
		        }
		        
				@Override				
		        public void onResponseReceived(Request request, Response response) {
		          if (200 == response.getStatusCode()) {
		        	  device.update(response.getText());
		            //updateTable(asArrayOfStockData(response.getText()));
		          } else {
		            //displayError("Couldn't retrieve JSON (" + response.getStatusText() + ")");
		          }
		        }

		      });
		    } catch (RequestException e) {
		      //displayError("Couldn't retrieve JSON");
		    }		
	}
	
	public static void sendData(String json, String url) {
		if (null == url) {
			MessageToast.message("No URL configured for device.");
			return;
		}
		 RequestBuilder builder = new RequestBuilder(RequestBuilder.PUT, url);
		 builder.setHeader("Content-Type", "application/json");
		 try {
			Request request = builder.sendRequest(json, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					if (200 == response.getStatusCode() || 204 == response.getStatusCode()) {
						//
					} else {
						MessageToast.alert("Status code: " + response.getStatusCode());
						//label.setText("" + response.getStatusCode());
			        }
					
				}

				@Override
				public void onError(Request request, Throwable exception) {
					MessageToast.alert("Error: " + exception.getMessage());
//					/label.setText(exception.getMessage());
					
				}});
		} catch (RequestException e) {
			// TODO Auto-generated catch block
		}		
	}

}
