package com.aeo.gestureaeo.controller;

import java.util.List;

import com.aeo.gestureaeo.Contact;
import com.aeo.gestureaeo.model.DataProvider;
import com.aeo.gestureaeo.view.adapter.ContactAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateGestureActivity extends Activity implements OnClickListener, OnGestureListener {

    private final float MINIMUM_STROKE_LENGHT = 150.0f;

    private GestureOverlayView gestureArea;
	private Spinner contacts;
	private Button  btnCreate;
	private Button  btnClear;
	private ImageView photo;
	
	private ContactAdapter adapter;
	private Gesture gesture; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_gesture_to_call);
				
        gestureArea = (GestureOverlayView) findViewById(R.id.set_gesture_to_call_gesture);
        contacts    = (Spinner) findViewById(R.id.set_gesture_to_call_favorite_contact);
        btnCreate   = (Button) findViewById(R.id.set_gesture_to_call_btn_create);
        btnClear   = (Button) findViewById(R.id.set_gesture_to_call_btn_clear);
        photo = (ImageView) findViewById(R.id.contact_photo);
        
        btnCreate.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        gestureArea.addOnGestureListener(this);
		
        List<Contact> data = DataProvider.getInstance().getContacts(this);
        
        if (data != null && data.size() > 0) {
    		contacts.setAdapter(adapter = new ContactAdapter(this, data));
        }
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);	    

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);	    

		MenuItem menuItemConfig = menu.findItem(R.id.menu_config);
	
		if (menuItemConfig != null) {
			menuItemConfig.setVisible(false);
		}
		
		return true;//super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
	    	case R.id.menu_call:
	    		finish();
	    		break;
	    	case R.id.menu_list:
	    		finish();
	    		startActivity(new Intent(this, ListGesturesActivity.class));
	    		break;
//	    	case R.id.menu_exit:
//	    		finish();
//	    		break;
	    }
		return true; 
	}
	
	@Override
	public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
        gesture = null;
	}

	@Override
	public void onGesture(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
		gesture = overlay.getGesture();
		if (gesture.getLength() < MINIMUM_STROKE_LENGHT) {
			overlay.clear(false);
		}
	
	}

	@Override
	public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		
		GestureOverlayView gest = (GestureOverlayView) findViewById(R.id.set_gesture_to_call_gesture);

		if (v == btnCreate) {
			if (gesture != null) {
				DataProvider.getInstance().save(this, (Contact) adapter.getItem(contacts.getSelectedItemPosition()), gesture);
				gest.cancelClearAnimation();
				gest.clear(true);
				Toast.makeText(this, "Gesto criado", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Gesto inválido", Toast.LENGTH_SHORT).show();
			}
		} else if (v == btnClear) { 	
			gest.cancelClearAnimation();
			gest.clear(true);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
}
