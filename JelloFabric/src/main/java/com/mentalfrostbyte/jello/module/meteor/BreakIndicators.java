package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Meteor Client module (Break Indicators). Behaviour port pending. */
public class BreakIndicators extends Module {
	private final ModeValue value1 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));
	private final BooleanValue value2 = addValue(new BooleanValue("packet-mine", true));

	public BreakIndicators() {
		super("Break Indicators", 0, Category.RENDER);
	}
}
