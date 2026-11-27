package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Sets gamma to the maximum while enabled. */
public class FullBright extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	private double previous;

	public FullBright() {
		super("FullBright", 0, Category.RENDER);
	}

	@Override
	public void onEnable() {
		if (mc.options != null) {
			previous = mc.options.gamma().get();
			mc.options.gamma().set(1.0);
		}
	}

	@Override
	public void onUpdate() {
		if (isToggled()) {
			mc.options.gamma().set(1.0);
		}
	}

	@Override
	public void onDisable() {
		if (mc.options != null) {
			mc.options.gamma().set(previous);
		}
	}
}
