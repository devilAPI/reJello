package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Anti Anchor). Behaviour port pending. */
public class AntiAnchor extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("rotate", true));
	private final BooleanValue value2 = addValue(new BooleanValue("swing", true));

	public AntiAnchor() {
		super("Anti Anchor", 0, Category.COMBAT);
	}
}
