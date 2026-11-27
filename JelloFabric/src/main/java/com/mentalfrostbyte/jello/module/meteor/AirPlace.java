package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Air Place). Behaviour port pending. */
public class AirPlace extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("render", true));
	private final ModeValue value2 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));
	private final BooleanValue value3 = addValue(new BooleanValue("custom-range", false));
	private final NumberValue value4 = addValue(new NumberValue("range", 5.0, 0.0, 100, 0.1));

	public AirPlace() {
		super("Air Place", 0, Category.PLAYER);
	}
}
