package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Replenish). Behaviour port pending. */
public class AutoReplenish extends Module {
	private final NumberValue value1 = addValue(new NumberValue("min-count", 8, 1, 63, 1));
	private final NumberValue value2 = addValue(new NumberValue("delay", 1, 0, 100, 1));
	private final BooleanValue value3 = addValue(new BooleanValue("offhand", true));
	private final BooleanValue value4 = addValue(new BooleanValue("unstackable", true));
	private final BooleanValue value5 = addValue(new BooleanValue("same-enchants", true));
	private final BooleanValue value6 = addValue(new BooleanValue("search-hotbar", false));

	public AutoReplenish() {
		super("Auto Replenish", 0, Category.PLAYER);
	}
}
