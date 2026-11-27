package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Boss Stack). Behaviour port pending. */
public class BossStack extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("stack", true));
	private final BooleanValue value2 = addValue(new BooleanValue("hide-name", false));
	private final NumberValue value3 = addValue(new NumberValue("bar-spacing", 10.0, 0.0, 100, 0.1));

	public BossStack() {
		super("Boss Stack", 0, Category.RENDER);
	}
}
