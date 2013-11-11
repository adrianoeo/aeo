package com.aeo.gestureaeo.view;

import java.util.HashMap;

import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Color;
import android.view.View.OnClickListener;

import com.aeo.gestureaeo.Action;
import com.aeo.gestureaeo.Key;
import com.aeo.gestureaeo.controller.R;
import com.aeo.gestureaeo.controller.ViewOwner;

public class ListenGestureView extends View implements OnClickListener, OnGesturePerformedListener  {

	private GestureOverlayView gestureArea;
	
	public ListenGestureView(ViewOwner owner) {
		super(owner);
		// Set layout.
        owner.setLayout(R.layout.gesture_to_act);
        
        // Get elements.
        gestureArea = (GestureOverlayView) owner.getViewLayout(R.id.gesture_to_act_gesture);
        // Set listeners.
        gestureArea.addOnGesturePerformedListener(this);        
        gestureArea.setUncertainGestureColor(Color.GREEN); 
        
	}

	@Override
	public void load(Object... data) {
		// Does nothing.
	}
	
	@Override
	public void onClick(android.view.View v) {
//		if (v == btnExit) {
//			owner.receiveAction(Action.EXIT, null);
//		} else if (v == btnSetup) {
//			owner.receiveAction(Action.SETUP, null);
//		}
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		HashMap<Key, Object> map = new HashMap<Key, Object>();
		map.put(Key.GESTURE_TO_ACT, gesture);
		owner.receiveAction(Action.GESTURE_TO_ACT, map);
	}
	
}