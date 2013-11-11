package com.aeo.gestureaeo.view.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aeo.gestureaeo.Contact;
import com.aeo.gestureaeo.controller.R;


public class ContactAdapter extends BaseAdapter {

	private Context context; 
	private List<Contact> list;
	
	public ContactAdapter(Context context, List<Contact> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return (list != null && !list.isEmpty()) ? list.size() : 0;
	}

	@Override
	public Contact getItem(int position) {
		return (list != null && !list.isEmpty()) ? list.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get elements.
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout root = (LinearLayout) inflate.inflate(R.layout.contact_item, null);
		TextView name     = (TextView) root.findViewById(R.id.contact_item_name);
		TextView phone    = (TextView) root.findViewById(R.id.contact_item_phone);
		ImageView photo   = (ImageView) root.findViewById(R.id.contact_photo);
		
		// Get item.
		Contact contact   = list.get(position);
		// Set values.
		name.setText(contact.getName());
		phone.setText(contact.getPhone());
		
		if (contact.getPhoto() != null) {
			photo.setImageBitmap(contact.getPhoto());
		} else {
			Bitmap btm = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_photo);
			
			photo.setImageBitmap(btm);
		}
		return root;
	}
}