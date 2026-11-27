package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Wasp). Behaviour port pending. */
public class AutoWasp extends Module {
	private final NumberValue value1 = addValue(new NumberValue("horizontal-speed", 2.0, 0, 100, 0.1));
	private final NumberValue value2 = addValue(new NumberValue("vertical-speed", 3.0, 0, 100, 0.1));
	private final BooleanValue value3 = addValue(new BooleanValue("avoid-landing", true));
	private final BooleanValue value4 = addValue(new BooleanValue("predict-movement", true));
	private final BooleanValue value5 = addValue(new BooleanValue("only-friends", false));
	private final ModeValue value6 = addValue(new ModeValue("action-on-target-loss", "TOGGLE", "TOGGLE", "CHOOSE_NEW_TARGET", "DISCONNECT"));

	public AutoWasp() {
		super("Auto Wasp", 0, Category.MOVEMENT);
	}
}
