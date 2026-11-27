package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Fish). Behaviour port pending. */
public class AutoFish extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("auto-switch", true));
	private final BooleanValue value2 = addValue(new BooleanValue("anti-break", true));
	private final BooleanValue value3 = addValue(new BooleanValue("auto-cast", true));
	private final NumberValue value4 = addValue(new NumberValue("cast-delay", 14, 1, 100, 1));
	private final NumberValue value5 = addValue(new NumberValue("cast-delay-variance", 0, 0, 100, 1));
	private final NumberValue value6 = addValue(new NumberValue("catch-delay", 6, 1, 100, 1));
	private final NumberValue value7 = addValue(new NumberValue("catch-delay-variance", 0, 0, 100, 1));

	public AutoFish() {
		super("Auto Fish", 0, Category.PLAYER);
	}
}
