package com.mentalfrostbyte.jello.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Dropdown/cycle setting (the 1.18 Reborn ModeValue). */
public class ModeValue extends Value {
	private final List<String> modes;
	private int index;

	public ModeValue(String name, String defaultMode, String... modes) {
		super(name);
		this.modes = new ArrayList<>(Arrays.asList(modes));
		int found = this.modes.indexOf(defaultMode);
		this.index = found < 0 ? 0 : found;
	}

	public String getMode() {
		return modes.isEmpty() ? "" : modes.get(index);
	}

	public boolean is(String mode) {
		return getMode().equalsIgnoreCase(mode);
	}

	public void cycle() {
		if (modes.isEmpty()) {
			return;
		}
		index = (index + 1) % modes.size();
	}

	public void setMode(String mode) {
		int found = modes.indexOf(mode);
		if (found >= 0) {
			index = found;
		}
	}

	public int getIndex() {
		return index;
	}

	public List<String> getModes() {
		return modes;
	}
}
