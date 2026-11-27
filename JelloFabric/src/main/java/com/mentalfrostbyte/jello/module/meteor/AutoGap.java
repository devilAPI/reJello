package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Gap). Behaviour port pending. */
public class AutoGap extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("allow-egap", true));
	private final BooleanValue value2 = addValue(new BooleanValue("always", false));
	private final BooleanValue value3 = addValue(new BooleanValue("pause-auras", true));
	private final BooleanValue value4 = addValue(new BooleanValue("pause-baritone", true));
	private final BooleanValue value5 = addValue(new BooleanValue("before-expiry", false));
	private final NumberValue value6 = addValue(new NumberValue("expiry-threshold", 60, 0, 100, 1));
	private final BooleanValue value7 = addValue(new BooleanValue("potions-regeneration", false));
	private final BooleanValue value8 = addValue(new BooleanValue("potions-fire-resistance", true));
	private final BooleanValue value9 = addValue(new BooleanValue("potions-absorption", false));
	private final BooleanValue value10 = addValue(new BooleanValue("health-enabled", true));
	private final NumberValue value11 = addValue(new NumberValue("health-threshold", 20, 0, 100, 1));

	public AutoGap() {
		super("Auto Gap", 0, Category.PLAYER);
	}
}
