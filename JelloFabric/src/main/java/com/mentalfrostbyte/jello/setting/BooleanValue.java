package com.mentalfrostbyte.jello.setting;

public class BooleanValue extends Value {
	private boolean value;

	public BooleanValue(String name, boolean value) {
		super(name);
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	public boolean isEnabled() {
		return value;
	}

	public void toggle() {
		value = !value;
	}

	public void set(boolean value) {
		this.value = value;
	}
}
