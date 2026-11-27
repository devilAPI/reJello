package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Trail). Behaviour port pending. */
public class Trail extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("pause-when-stationary", true));

	public Trail() {
		super("Trail", 0, Category.RENDER);
	}
}
