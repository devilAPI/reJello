package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/** Holds the sneak key down while enabled. */
public class FastSneak extends Module {
	public FastSneak() {
		super("FastSneak", 0, Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		if (isToggled()) {
			mc.options.keyShift.setDown(true);
		}
	}

	@Override
	public void onDisable() {
		mc.options.keyShift.setDown(false);
	}
}
