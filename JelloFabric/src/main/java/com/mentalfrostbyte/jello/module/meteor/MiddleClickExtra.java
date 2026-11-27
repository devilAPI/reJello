package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Meteor Client module (Middle Click Extra). Behaviour port pending. */
public class MiddleClickExtra extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Pearl", "Pearl", "C", "T", "B"));
	private final BooleanValue value2 = addValue(new BooleanValue("send-message", false));
	private final BooleanValue value3 = addValue(new BooleanValue("quick-swap", false));
	private final BooleanValue value4 = addValue(new BooleanValue("swap-back", false));
	private final BooleanValue value5 = addValue(new BooleanValue("disable-in-creative", true));
	private final BooleanValue value6 = addValue(new BooleanValue("notify", true));

	public MiddleClickExtra() {
		super("Middle Click Extra", 0, Category.PLAYER);
	}
}
