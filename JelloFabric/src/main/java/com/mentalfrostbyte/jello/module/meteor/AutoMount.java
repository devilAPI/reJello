package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Auto Mount). Behaviour port pending. */
public class AutoMount extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("check-saddle", false));
	private final BooleanValue value2 = addValue(new BooleanValue("rotate", true));

	public AutoMount() {
		super("Auto Mount", 0, Category.WORLD);
	}
}
