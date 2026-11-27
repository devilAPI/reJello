package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Detached camera. The 26.3 camera is rebuilt from the render state, so this is a placeholder. */
public class Freecam extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	public Freecam() {
		super("Freecam", 0, Category.RENDER);
	}
}
