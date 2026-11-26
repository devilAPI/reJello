package com.mentalfrostbyte.jello.setting;

public abstract class Value {
	private final String name;

	protected Value(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
