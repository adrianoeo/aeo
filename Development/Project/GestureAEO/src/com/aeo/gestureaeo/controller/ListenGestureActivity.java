package com.aeo.gestureaeo.controller;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.aeo.gestureaeo.Action;
import com.aeo.gestureaeo.Contact;
import com.aeo.gestureaeo.Key;
import com.aeo.gestureaeo.model.DataProvider;
import com.aeo.gestureaeo.view.ListenGestureView;
import com.aeo.gestureaeo.view.CreateGestureToCallView;
import com.aeo.gestureaeo.view.View;

public class ListenGestureActivity extends Activity implements ViewOwner {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setLocate();
        
        new ListenGestureView(this);
    }

    public void setLocate() {
    	Configuration conf = getResources().getConfiguration();
    	Locale locale = conf.locale;
    	if (!locale.getLanguage().equals("pt")) {
        	conf.locale = Locale.ENGLISH;
        	getBaseContext().getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
    	}
    	
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
    	if (!newConfig.locale.getLanguage().equals("pt")) {
    		newConfig.locale = Locale.ENGLISH;
    		super.onConfigurationChanged(newConfig);

    		Locale.setDefault(newConfig.locale);
    		getBaseContext().getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
    	}
	}

	@Override
	public void setLayout(int id) {
		setContentView(id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);	    

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.findItem(R.id.menu_call);
	
		if (menuItem != null) {
			menuItem.setVisible(false);
		}
		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
	    	case R.id.menu_config:
//	    		Toast.makeText(this, "configurando", Toast.LENGTH_SHORT).show();
//	            View view = new CreateGestureToCallView(this);
//	            view.load(DataProvider.getInstance().getContacts(this));
	    		startActivity(new Intent(this, CreateGestureActivity.class));
	    		break;
	    	case R.id.menu_list:
	    		startActivity(new Intent(this, ListGesturesActivity.class));
	    		break;
//	    	case R.id.menu_exit:
//	    		finish();
//	    		break;
	    }
		return true; 
	}
	
	@Override
	public android.view.View getViewLayout(int id) {
		return findViewById(id);
	}
	
	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public void receiveAction(Action action, HashMap<Key, Object> map) {
		switch(action) {
			case CREATE_GESTURE_TO_CALL :
				// Save data.
//				DataProvider.getInstance().save(this, (Contact) map.get(Key.CONTACT_TO_SAVE));
//				DataProvider.getInstance().save(this, Key.GESTURE_TO_CALL, (Gesture) map.get(Key.GESTURE_TO_CALL));
				DataProvider.getInstance().save(this, (Contact) map.get(Key.CONTACT_TO_SAVE), (Gesture) map.get(Key.GESTURE_TO_CALL));
				
				GestureOverlayView gesture = (GestureOverlayView) findViewById(R.id.set_gesture_to_call_gesture);
				gesture.cancelClearAnimation();
				gesture.clear(true);
				Toast.makeText(this, "Gesto criado", Toast.LENGTH_SHORT).show();
				break;
			case GESTURE_TO_ACT :
				DataProvider model = DataProvider.getInstance();
				Contact contact = model.getContact(this, (Gesture) map.get(Key.GESTURE_TO_ACT));
				if (contact != null) {
					Toast.makeText(this, getString(R.string.gesture_to_call) + " " + contact.getName(), Toast.LENGTH_SHORT).show();
				    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getPhone())));
				} else  {
					Toast.makeText(this, getString(R.string.gesture_not_found), Toast.LENGTH_SHORT).show();
				}
				break;
				
			case EXIT :
				finish();
				break;
			default:
				break;
		}
	}

		
}