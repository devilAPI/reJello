package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Anchor). Behaviour port pending. */
public class Anchor extends Module {
	private final NumberValue value1 = addValue(new NumberValue("max-height", 10, 0, 255, 1));
	private final NumberValue value2 = addValue(new NumberValue("min-pitch", 0, -90, 90, 1));
	private final BooleanValue value3 = addValue(new BooleanValue("cancel-jump-in-hole", false));
	private final BooleanValue value4 = addValue(new BooleanValue("pull", false));
	private final NumberValue value5 = addValue(new NumberValue("pull-speed", 0.3, 0.0, 100, 0.1));

	public Anchor() {
		super("Anchor", 0, Category.MOVEMENT);
	}
}
