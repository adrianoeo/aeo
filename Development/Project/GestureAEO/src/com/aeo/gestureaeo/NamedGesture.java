package com.aeo.gestureaeo;

import android.gesture.Gesture;

public class NamedGesture {
    private String name;
    private Gesture gesture;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Gesture getGesture() {
		return gesture;
	}
	public void setGesture(Gesture gesture) {
		this.gesture = gesture;
	}

}
