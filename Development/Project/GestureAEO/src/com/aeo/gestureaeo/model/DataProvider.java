package com.aeo.gestureaeo.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.widget.Toast;

import com.aeo.gestureaeo.Action;
import com.aeo.gestureaeo.Contact;
import com.aeo.gestureaeo.Key;
import com.aeo.gestureaeo.controller.R;

@SuppressLint("NewApi")
public class DataProvider {

	private final String CONTACT_STORE = "contact.store";
	private final static String FOLDER_LIB = "/GestCalAEO/";
	private final static String GESTURE_LIB = "gesturesAEO.lib";
	
	private static DataProvider instance;
	private static GestureLibrary gestureLib;
	private final double MINIMUM_SCORE = 3.5D;
	
    private final static File mStoreFile = new File(Environment.getExternalStorageDirectory() + FOLDER_LIB, GESTURE_LIB);
	
	private DataProvider() {
	}

	public static synchronized DataProvider getInstance() {
		if (instance == null) {
			instance = new DataProvider();
		}

		if (gestureLib == null) {
			gestureLib = GestureLibraries.fromFile(mStoreFile);
		}
		
		return instance;
	}
	
	public List<Contact> getContacts(Context context) {
		// Set query.
		final Uri URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		final String[] COLUMNS  = new String[]{
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID, 
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, 
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID};
		final String   WHERE    = new String(Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " and '" + Data.STARRED + "' = '" + ContactsContract.CommonDataKinds.Phone.STARRED +"'");
		final String   ORBER_BY = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"; 

		// Run query.
		Cursor c = context.getContentResolver().query(URI, COLUMNS, WHERE, null, ORBER_BY);
		List<Contact> contacts = new ArrayList<Contact>();
		
		Bitmap photo = null;
		
		try {
			if (c != null && c.getCount() <= 0) {
				return null;
			}
			
			while (c.moveToNext()) {
				if (c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID)) != null) {
					photo = queryPhotoContact(context, c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID)));
				} else {
					photo = null;
				}
				
				contacts.add(new Contact(
					c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)), 
					c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)), 
					c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
					photo
				));
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		
		return contacts;
	}
	
	public Bitmap queryPhotoContact(Context context, String idPhoto) {
	    Cursor c = context.getContentResolver().query(
	    		ContactsContract.Data.CONTENT_URI, new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO}, 
	    		ContactsContract.Data._ID + "=?", new String[] {idPhoto}, null);
        byte[] imageBytes = null;
        
        if (c != null) {
            if (c.moveToFirst()) {
                imageBytes = c.getBlob(0);
            }
            c.close();
        }

        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length); 
        } else {
            return null;
        }
        
	}
/*
	public List<Contact> getContactsOrderByLastTimeContacted(Context context, int limit) {
		// Set query.
		final Uri URI = CallLog.Calls.CONTENT_URI;
		final String[] COLUMNS  = new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER};
		final String   ORBER_BY = CallLog.Calls.DATE + " DESC LIMIT " + limit; 
		// Run query.
		Cursor c = context.getContentResolver().query(URI, COLUMNS, null, null, ORBER_BY);
		if (c != null && c.getCount() <= 0) {
			return null;
		}
		List<Contact> contacts = new ArrayList<Contact>();
		while (c.moveToNext()) {
			contacts.add(new Contact(
				c.getString(c.getColumnIndex(CallLog.Calls.)), 
				c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME)), 
				c.getString(c.getColumnIndex(CallLog.Calls.NUMBER))
			));
		}
		return contacts;
	}
*/	
/*	
	public Contact getContact(Context context) {
		SharedPreferences pref = context.getSharedPreferences(CONTACT_STORE, Context.MODE_PRIVATE);
		return new Contact(
			pref.getString(Key.CONTACT_ID.name(), null), 
			pref.getString(Key.CONTACT_NAME.name(), null), 
			pref.getString(Key.CONTACT_PHONE.name(), null)
		); 
	}
*/	
	public Contact getContact(Context context, Gesture gesture) {
		
		gestureLib.load();
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		
		Prediction prediction = (predictions.size() > 0) ? predictions.get(0) : null;
	    prediction = (prediction != null && prediction.score > MINIMUM_SCORE) ? prediction : null;
	    Contact contact = null;
	    
		if (prediction != null) {
			final Uri URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			final String[] COLUMNS  = new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
			final String   WHERE    = new String(ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = '" + prediction.name + "'");

			Cursor c = context.getContentResolver().query(URI, COLUMNS, WHERE, null, null);
			
			try {
				if (c != null)
					if (c.getCount() > 0) {
						if (c.moveToFirst()) {
							contact = new Contact(
								c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)), 
								c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)), 
								c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
								null);
						}
				}
			} finally {
				if (c != null) {
					c.close();
				}
			}
		}
		
		return contact;
	}
	
	public String getContactName(Context context, String idContact) {
		StringBuilder contactName = new StringBuilder();
		
		final Uri URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		final String[] COLUMNS  = new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
		final String   WHERE    = new String(ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = '" + idContact + "'");
		
		Cursor c = context.getContentResolver().query(URI, COLUMNS, WHERE, null, null);
		try {
			if (c != null) {
				if (c.getCount() > 0) {
					if (c.moveToFirst()) {
						contactName.append(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))
							.append("|")
							.append(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))
							.append("|")
							.append(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
					}
				}
			} 
			return contactName.toString();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		
		
	}
	
	public Action getAction(Context context, Gesture gesture) {
//		loadGestureStore(context);
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		if (predictions.size() > 0) {
			Prediction prediction = predictions.get(0);
			if (prediction.score > MINIMUM_SCORE) {
				final String ACTION = prediction.name; 
				if (ACTION.equals(Action.GESTURE_TO_CALL.name())) {
					return Action.GESTURE_TO_CALL;
				} else if (ACTION.equals(Action.GESTURE_TO_LIST.name())) {
					return Action.GESTURE_TO_LIST;
				}
			}
		}
		return null;
	}	
	public boolean hasFavoriteContact(Context context) {
		SharedPreferences pref = context.getSharedPreferences(CONTACT_STORE, Context.MODE_PRIVATE);
		return pref.contains(Key.CONTACT_NAME.name()) && pref.contains(Key.CONTACT_PHONE.name()); 
	}
	
	public void save(Context context, Contact contact) {
		SharedPreferences pref = context.getSharedPreferences(CONTACT_STORE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(Key.CONTACT_NAME.name(), contact.getName());
		editor.putString(Key.CONTACT_PHONE.name(), contact.getPhone());
		editor.commit();
	}
	

	public GestureLibrary loadGestureStore() {
		return gestureLib;
	}
	
	public void save(Context context, Key action, Gesture gesture) {
//		loadGestureStore(context);

		gestureLib.removeEntry(action.name());
		gestureLib.addGesture(action.name(), gesture);
        gestureLib.save();
	}

	public void save(Context context, Contact contact, Gesture gesture) {
		gestureLib.load();
		gestureLib.removeEntry(contact.getIdContact());
		gestureLib.addGesture(contact.getIdContact(), gesture);
        gestureLib.save();
	}

	public static File getMstorefile() {
		return mStoreFile;
	}
	
	public void delete(String contactId) {
		gestureLib.load();
		gestureLib.removeEntry(contactId);
        gestureLib.save();
	}

}