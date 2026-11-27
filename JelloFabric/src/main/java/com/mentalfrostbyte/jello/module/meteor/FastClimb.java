package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Fast Climb). Behaviour port pending. */
public class FastClimb extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("timer-mode", false));
	private final NumberValue value2 = addValue(new NumberValue("climb-speed", 0.2872, 0.0, 100, 0.1));
	private final NumberValue value3 = addValue(new NumberValue("timer", 1.436, 1.0, 100, 0.1));

	public FastClimb() {
		super("Fast Climb", 0, Category.MOVEMENT);
	}
}
