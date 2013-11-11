package com.aeo.gestureaeo;

import android.graphics.Bitmap;

public class Contact {

	private String idContact;
	private String name;
	private String phone;
	private Bitmap photo;
	
	public Contact(String idContact, String name, String phone, Bitmap photo) {
		super();
		this.idContact = idContact;
		this.name  = name;
		this.phone = phone;
		this.photo = photo;
	}

	public String getName() {
		return name;
	}
	
	public String getPhone() {
		return phone;
	}

	
	public String getIdContact() {
		return idContact;
	}

	@Override
	public String toString() {
		return "Contact [name=" + name + ", phone=" + phone + "]";
	}

	public Bitmap getPhoto() {
		return photo;
	}

}