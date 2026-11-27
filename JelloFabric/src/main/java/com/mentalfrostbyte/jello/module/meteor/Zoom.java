package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Zoom). Behaviour port pending. */
public class Zoom extends Module {
	private final NumberValue value1 = addValue(new NumberValue("zoom", 6.0, 1.0, 100, 0.1));
	private final NumberValue value2 = addValue(new NumberValue("scroll-sensitivity", 1.0, 0.0, 100, 0.1));
	private final BooleanValue value3 = addValue(new BooleanValue("smooth", true));
	private final BooleanValue value4 = addValue(new BooleanValue("cinematic", false));
	private final BooleanValue value5 = addValue(new BooleanValue("hide-HUD", false));
	private final BooleanValue value6 = addValue(new BooleanValue("show-hands", false));

	public Zoom() {
		super("Zoom", 0, Category.RENDER);
	}
}
