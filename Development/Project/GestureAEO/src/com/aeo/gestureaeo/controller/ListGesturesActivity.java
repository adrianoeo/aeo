package com.aeo.gestureaeo.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.aeo.gestureaeo.NamedGesture;
import com.aeo.gestureaeo.model.DataProvider;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ListGesturesActivity extends ListActivity {

	private static final int STATUS_SUCCESS = 0;
    private static final int STATUS_CANCELLED = 1;
    private static final int STATUS_NO_STORAGE = 2;
    private static final int STATUS_NOT_LOADED = 3;

    private GesturesAdapter mAdapter;
    private GesturesLoadTask mTask;
    private GestureLibrary gestureLib;
    private TextView mEmpty;

    
    private final Comparator<NamedGesture> mSorter = new Comparator<NamedGesture>() {
        public int compare(NamedGesture object1, NamedGesture object2) {
            return object1.getName().compareTo(object2.getName());
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gestures_list);

        mAdapter = new GesturesAdapter(this);
        setListAdapter(mAdapter);
		
        gestureLib = DataProvider.getInstance().loadGestureStore();
        
        mEmpty = (TextView) findViewById(android.R.id.empty);
        
        loadGestures();

        registerForContextMenu(getListView());
        
	}

	public Context getContext() {
		return this;
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

		MenuItem menuItemList = menu.findItem(R.id.menu_list);
	
		if (menuItemList != null) {
			menuItemList.setVisible(false);
		}
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
	    	case R.id.menu_call:
	    		finish();
	    		break;
	    	case R.id.menu_config:
	    		finish();
	    		startActivity(new Intent(this, CreateGestureActivity.class));
	    		break;
//	    	case R.id.menu_exit:
//	    		finish();
//	    		break;
	    }
		return true; 
	}	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			//startActivity(new Intent(this, ListenGestureActivity.class));
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
    private void loadGestures() {
        if (mTask != null && mTask.getStatus() != GesturesLoadTask.Status.FINISHED){
            mTask.cancel(true);
        }        
        mTask = (GesturesLoadTask) new GesturesLoadTask().execute();
    }

    private class GesturesLoadTask extends AsyncTask<Void, NamedGesture, Integer> {
        private int mThumbnailSize;
        private int mThumbnailInset;
        private int mPathColor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            final Resources resources = getResources();
            mPathColor = resources.getColor(R.color.gesture_color);
            mThumbnailInset = (int) resources.getDimension(R.dimen.gesture_thumbnail_inset);
            mThumbnailSize = (int) resources.getDimension(R.dimen.gesture_thumbnail_size);

            mAdapter.setNotifyOnChange(false);            
            mAdapter.clear();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (isCancelled()) return STATUS_CANCELLED;
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return STATUS_NO_STORAGE;
            }

            final GestureLibrary store = gestureLib;
            
            if (store.load()) {
            	for (String name : store.getGestureEntries()) {
            		if (isCancelled()) break;

            		for (Gesture gesture : store.getGestures(name)) {
            			String contactName = DataProvider.getInstance().getContactName(getContext(), name);
            			if (contactName != null && !"".equals(contactName)) {
                			final Bitmap bitmap = gesture.toBitmap(mThumbnailSize, mThumbnailSize, mThumbnailInset, mPathColor);
                			final NamedGesture namedGesture = new NamedGesture();
                			namedGesture.setGesture(gesture);
            				namedGesture.setName(contactName);	
                			mAdapter.addBitmap(namedGesture.getGesture().getID(), bitmap);
                			publishProgress(namedGesture);
            			} else {
            				DataProvider.getInstance().delete(name);
            			}
            		}
            	}
                return STATUS_SUCCESS;
            }

            return STATUS_NOT_LOADED;
        }

        @Override
        protected void onProgressUpdate(NamedGesture... values) {
            super.onProgressUpdate(values);

            final GesturesAdapter adapter = mAdapter;
            adapter.setNotifyOnChange(false);

            for (NamedGesture gesture : values) {
                adapter.add(gesture);
            }

            adapter.sort(mSorter);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if (result == STATUS_NO_STORAGE) {
                getListView().setVisibility(View.GONE);
                mEmpty.setVisibility(View.VISIBLE);
                mEmpty.setText(R.string.gestures_error_loading);
            } else {
                checkForEmpty();
            }
        }
    }
	
    private void checkForEmpty() {
        if (mAdapter.getCount() == 0) {
            mEmpty.setText(R.string.gestures_empty);
        }
    }
	
    private class GesturesAdapter extends ArrayAdapter<NamedGesture> implements OnClickListener {
        private final LayoutInflater mInflater;
        private final Map<Long, Drawable> mThumbnails = Collections.synchronizedMap(
                new HashMap<Long, Drawable>());

        private NamedGesture gesture;
        private TextView label;
        private TextView labelID;
        private TextView labelPhone;
        private LinearLayout layout;
        
        public GesturesAdapter(Context context) {
            super(context, 0);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        void addBitmap(Long id, Bitmap bitmap) {
            mThumbnails.put(id, new BitmapDrawable(getContext().getResources(), bitmap));
//            mThumbnails.put(id, new BitmapDrawable(bitmap));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.gestures_item, parent, false);
            }
           
            gesture = getItem(position);
            label = (TextView) convertView.findViewById(R.id.text1);
            labelID = (TextView) convertView.findViewById(R.id.contact_id);
            labelPhone = (TextView) convertView.findViewById(R.id.contact_phone);
            layout = (LinearLayout) convertView.findViewById(R.id.layout_gestures_item);            
            
            layout.setOnClickListener(this);
            
            
            String[] contact = gesture.getName().split("\\|");
            
            label.setTag(gesture);
            label.setText(contact[0]);
            labelPhone.setText(contact[1]);
            labelID.setText(contact[2]);
            
            label.setCompoundDrawablesWithIntrinsicBounds(mThumbnails.get(gesture.getGesture().getID()),
                    null, null, null);

            return convertView;
        }

		@Override
		public void onClick(View v) {
			final TextView textID = (TextView) v.findViewById(R.id.contact_id);
			TextView textName = (TextView) v.findViewById(R.id.text1);
			TextView textPhone = (TextView) v.findViewById(R.id.contact_phone);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			
			builder.setPositiveButton(R.string.btn_dialog_delete, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DataProvider.getInstance().delete(textID.getText().toString());
					loadGestures();
				}
			});

			builder.setNegativeButton(R.string.btn_dialog_back, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			builder.setMessage(textName.getText().toString() + " (" + textPhone.getText().toString() + ")").setTitle("Contato");
			
			AlertDialog dialog = builder.create();
			
			dialog.show();
			
//			Toast.makeText(getContext(), textID.getText().toString(), Toast.LENGTH_SHORT).show();
		}

    }	

}
