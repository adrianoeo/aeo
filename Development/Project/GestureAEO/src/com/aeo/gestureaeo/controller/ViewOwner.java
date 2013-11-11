package com.aeo.gestureaeo.controller;

import java.util.HashMap;

import com.aeo.gestureaeo.Action;
import com.aeo.gestureaeo.Key;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.view.Menu;
import android.view.View;

public interface ViewOwner {
	
	public void receiveAction(Action action, HashMap<Key, Object> map);
	
	public void setLayout(int id);
	
	public View getViewLayout(int id);
	
	public Context getContext();
		
}