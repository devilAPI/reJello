package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Arrow Dodge). Behaviour port pending. */
public class ArrowDodge extends Module {
	private final ModeValue value1 = addValue(new ModeValue("move-type", "Velocity", "Velocity", "V", "P"));
	private final NumberValue value2 = addValue(new NumberValue("move-speed", 1.0, 0.01, 5.0, 0.1));
	private final NumberValue value3 = addValue(new NumberValue("distance-check", 1.0, 0.01, 5.0, 0.1));
	private final BooleanValue value4 = addValue(new BooleanValue("ground-check", true));
	private final BooleanValue value5 = addValue(new BooleanValue("all-projectiles", false));
	private final BooleanValue value6 = addValue(new BooleanValue("ignore-own", true));
	private final NumberValue value7 = addValue(new NumberValue("simulation-steps", 100, 0, 100, 1));

	public ArrowDodge() {
		super("Arrow Dodge", 0, Category.COMBAT);
	}
}
