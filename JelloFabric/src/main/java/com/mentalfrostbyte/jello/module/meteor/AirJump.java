package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Air Jump). Behaviour port pending. */
public class AirJump extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("maintain-level", false));

	public AirJump() {
		super("Air Jump", 0, Category.MOVEMENT);
	}
}
