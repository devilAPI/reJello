package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Reverse Step). Behaviour port pending. */
public class ReverseStep extends Module {
	private final NumberValue value1 = addValue(new NumberValue("fall-speed", 3.0, 0.0, 100, 0.1));
	private final NumberValue value2 = addValue(new NumberValue("fall-distance", 3.0, 0.0, 100, 0.1));
	private final BooleanValue value3 = addValue(new BooleanValue("vehicles", false));

	public ReverseStep() {
		super("Reverse Step", 0, Category.MOVEMENT);
	}
}
