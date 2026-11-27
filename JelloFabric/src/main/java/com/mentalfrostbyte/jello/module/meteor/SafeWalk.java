package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Safe Walk). Behaviour port pending. */
public class SafeWalk extends Module {
	private final NumberValue value1 = addValue(new NumberValue("minimum-fall-distance", 1, 1, 100, 1));
	private final BooleanValue value2 = addValue(new BooleanValue("sneak", false));
	private final BooleanValue value3 = addValue(new BooleanValue("safe-sneak", true));
	private final BooleanValue value4 = addValue(new BooleanValue("sneak-on-sprint", true));
	private final NumberValue value5 = addValue(new NumberValue("edge-distance", 0.3, 0.0, 0.3, 0.1));
	private final BooleanValue value6 = addValue(new BooleanValue("render", false));
	private final BooleanValue value7 = addValue(new BooleanValue("render-player-box", false));

	public SafeWalk() {
		super("Safe Walk", 0, Category.MOVEMENT);
	}
}
