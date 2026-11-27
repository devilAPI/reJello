package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Shearer). Behaviour port pending. */
public class AutoShearer extends Module {
	private final NumberValue value1 = addValue(new NumberValue("distance", 5.0, 0.0, 100, 0.1));
	private final BooleanValue value2 = addValue(new BooleanValue("anti-break", false));
	private final BooleanValue value3 = addValue(new BooleanValue("rotate", true));

	public AutoShearer() {
		super("Auto Shearer", 0, Category.WORLD);
	}
}
