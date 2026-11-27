package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Reborn's FastUse sent extra C03 packets; 26.x item use is server-timed. Kept for the module list. */
public class FastUse extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	public FastUse() {
		super("FastUse", 0, Category.PLAYER);
		addValue(new com.mentalfrostbyte.jello.setting.ModeValue("Mode", "Instant", "Instant", "Matrix"));
	}
}
