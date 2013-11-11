package com.aeo.gestureaeo.view;

import com.aeo.gestureaeo.controller.ViewOwner;

public abstract class View {

	protected ViewOwner owner;
	
	public View(ViewOwner owner) {
		this.owner = owner;
	}
	
	public abstract void load(Object...data);
}
