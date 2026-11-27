package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Jump). Behaviour port pending. */
public class AutoJump extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Jump", "Jump", "C", "T", "B"));
	private final ModeValue value2 = addValue(new ModeValue("jump-if", "Always", "Always", "S", "W", "A"));
	private final NumberValue value3 = addValue(new NumberValue("velocity-height", 0.25, 0.0, 100, 0.1));

	public AutoJump() {
		super("Auto Jump", 0, Category.MOVEMENT);
	}
}
