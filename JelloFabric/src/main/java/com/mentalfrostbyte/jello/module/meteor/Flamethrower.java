package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Flamethrower). Behaviour port pending. */
public class Flamethrower extends Module {
	private final NumberValue value1 = addValue(new NumberValue("distance", 5.0, 0.0, 100, 0.1));
	private final BooleanValue value2 = addValue(new BooleanValue("anti-break", false));
	private final BooleanValue value3 = addValue(new BooleanValue("put-out-fire", true));
	private final BooleanValue value4 = addValue(new BooleanValue("target-babies", false));
	private final NumberValue value5 = addValue(new NumberValue("tick-interval", 5, 0, 100, 1));
	private final BooleanValue value6 = addValue(new BooleanValue("rotate", true));

	public Flamethrower() {
		super("Flamethrower", 0, Category.WORLD);
	}
}
