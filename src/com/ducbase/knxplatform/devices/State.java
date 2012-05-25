package com.ducbase.knxplatform.devices;

public class State<T> {
	
	private T state;

	public T getState() {
		return state;
	}

	public void setState(T state) {
		this.state = state;
	}

}
