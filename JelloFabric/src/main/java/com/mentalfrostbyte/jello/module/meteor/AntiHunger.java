package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Anti Hunger). Behaviour port pending. */
public class AntiHunger extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("sprint", true));
	private final BooleanValue value2 = addValue(new BooleanValue("on-ground", true));

	public AntiHunger() {
		super("Anti Hunger", 0, Category.PLAYER);
	}
}
