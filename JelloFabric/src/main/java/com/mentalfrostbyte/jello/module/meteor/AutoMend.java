package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Auto Mend). Behaviour port pending. */
public class AutoMend extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("force", false));
	private final BooleanValue value2 = addValue(new BooleanValue("auto-disable", true));

	public AutoMend() {
		super("Auto Mend", 0, Category.PLAYER);
	}
}
