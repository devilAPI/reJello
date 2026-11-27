package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Exp). Behaviour port pending. */
public class AutoEXP extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Both", "Both", "C", "T", "B"));
	private final BooleanValue value2 = addValue(new BooleanValue("replenish", true));
	private final BooleanValue value3 = addValue(new BooleanValue("only-on-ground", false));
	private final NumberValue value4 = addValue(new NumberValue("exp-slot", 6, 1, 9, 1));
	private final NumberValue value5 = addValue(new NumberValue("min-threshold", 30, 1, 100, 1));
	private final NumberValue value6 = addValue(new NumberValue("max-threshold", 80, 1, 100, 1));

	public AutoEXP() {
		super("Auto Exp", 0, Category.COMBAT);
	}
}
