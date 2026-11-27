package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Break Delay). Behaviour port pending. */
public class BreakDelay extends Module {
	private final NumberValue value1 = addValue(new NumberValue("cooldown", 0, 0, 100, 1));
	private final BooleanValue value2 = addValue(new BooleanValue("no-insta-break", false));

	public BreakDelay() {
		super("Break Delay", 0, Category.PLAYER);
	}
}
