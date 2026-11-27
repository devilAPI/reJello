package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Spider). Behaviour port pending. */
public class Spider extends Module {
	private final NumberValue value1 = addValue(new NumberValue("climb-speed", 0.2, 0.0, 100, 0.1));

	public Spider() {
		super("Spider", 0, Category.MOVEMENT);
	}
}
