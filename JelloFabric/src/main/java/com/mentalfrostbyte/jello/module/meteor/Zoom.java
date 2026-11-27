package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client zoom. */
public class Zoom extends Module {
	private final NumberValue level = addValue(new NumberValue("Zoom Level", 4.0, 1.5, 10.0, 0.5));
	private int previousFov;

	public Zoom() {
		super("Zoom", 0, Category.RENDER);
	}

	@Override
	public void onEnable() {
		if (mc.options != null) {
			previousFov = mc.options.fov().get();
			mc.options.fov().set((int) Math.max(10, previousFov / level.getValue()));
		}
	}

	@Override
	public void onDisable() {
		if (mc.options != null) {
			mc.options.fov().set(previousFov);
		}
	}
}
