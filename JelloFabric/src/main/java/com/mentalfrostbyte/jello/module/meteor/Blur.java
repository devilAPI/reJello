package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Blur). Behaviour port pending. */
public class Blur extends Module {
	private final NumberValue value1 = addValue(new NumberValue("strength", 5, 1, 20, 1));
	private final NumberValue value2 = addValue(new NumberValue("fade-time", 100, 0, 100, 1));
	private final BooleanValue value3 = addValue(new BooleanValue("meteor", true));
	private final BooleanValue value4 = addValue(new BooleanValue("inventories", true));
	private final BooleanValue value5 = addValue(new BooleanValue("chat", false));
	private final BooleanValue value6 = addValue(new BooleanValue("other", true));

	public Blur() {
		super("Blur", 0, Category.RENDER);
	}
}
