package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Sign). Behaviour port pending. */
public class AutoSign extends Module {
	private final NumberValue value1 = addValue(new NumberValue("delay", 10, 0, 100, 1));

	public AutoSign() {
		super("Auto Sign", 0, Category.WORLD);
	}
}
