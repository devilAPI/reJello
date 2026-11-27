package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Xray). Behaviour port pending. */
public class Xray extends Module {
	private final NumberValue value1 = addValue(new NumberValue("opacity", 25, 0, 255, 1));
	private final ModeValue value2 = addValue(new ModeValue("fluid-opacity", "Both", "Both", "N", "W", "L", "B"));
	private final BooleanValue value3 = addValue(new BooleanValue("exposed-only", false));

	public Xray() {
		super("Xray", 0, Category.RENDER);
	}
}
