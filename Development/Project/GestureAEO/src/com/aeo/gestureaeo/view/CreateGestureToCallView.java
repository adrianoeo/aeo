/*Não está sendo utilizado*/

package com.aeo.gestureaeo.view;

import java.util.HashMap;
import java.util.List;

import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;

import com.aeo.gestureaeo.Action;
import com.aeo.gestureaeo.Key;
import com.aeo.gestureaeo.Contact;
import com.aeo.gestureaeo.controller.R;
import com.aeo.gestureaeo.controller.ViewOwner;
import com.aeo.gestureaeo.view.adapter.ContactAdapter;

public class CreateGestureToCallView extends View implements OnClickListener, OnGestureListener  {

    private final float MINIMUM_STROKE_LENGHT = 150.0f;
	
	private GestureOverlayView gestureArea;
	private Spinner contacts;
	private Button  btnCreate;
	private Button  btnCancel;
	
	private ContactAdapter adapter;
	private Gesture gesture; 
	
	public CreateGestureToCallView(ViewOwner owner) {
		super(owner);
		// Set layout.
        owner.setLayout(R.layout.set_gesture_to_call);
        // Get elements.
        gestureArea = (GestureOverlayView) owner.getViewLayout(R.id.set_gesture_to_call_gesture);
        contacts    = (Spinner) owner.getViewLayout(R.id.set_gesture_to_call_favorite_contact);
        btnCreate   = (Button) owner.getViewLayout(R.id.set_gesture_to_call_btn_create);
        
        // Set listeners.
        btnCreate.setOnClickListener(this);
        gestureArea.addOnGestureListener(this);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void load(Object... data) {
        if (data != null && data.length > 0) {
    		contacts.setAdapter(adapter = new ContactAdapter(owner.getContext(), (List<Contact>) data[0]));
        }
	}
	
	@Override
	public void onClick(android.view.View v) {
		if (v == btnCreate) {
			HashMap<Key, Object> map = new HashMap<Key, Object>();
			map.put(Key.GESTURE_TO_CALL, gesture);
			map.put(Key.CONTACT_TO_SAVE, adapter.getItem(contacts.getSelectedItemPosition()));
			owner.receiveAction(Action.CREATE_GESTURE_TO_CALL, map);
		} /*else if (v == btnCancel) {
			owner.receiveAction(Action.BACK, null);
		}*/
	}

	@Override
	public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
        gesture = null;
	}

	@Override
	public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
		gesture = overlay.getGesture();
		if (gesture.getLength() < MINIMUM_STROKE_LENGHT) {
			overlay.clear(false);
		}
	}
	
	@Override
	public void onGesture(GestureOverlayView overlay, MotionEvent event) {
		// Does nothing.
	}

	@Override
	public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
		// Does nothing.
	}
}