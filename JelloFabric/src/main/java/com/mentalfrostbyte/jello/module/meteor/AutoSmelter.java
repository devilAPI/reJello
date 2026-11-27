package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Smelter). Behaviour port pending. */
public class AutoSmelter extends Module {
	private final NumberValue value1 = addValue(new NumberValue("fuel-items-per-refill", 64, 1, 64, 1));
	private final BooleanValue value2 = addValue(new BooleanValue("disable-when-out-of-items", true));
	private final BooleanValue value3 = addValue(new BooleanValue("auto-close", false));

	public AutoSmelter() {
		super("Auto Smelter", 0, Category.WORLD);
	}
}
