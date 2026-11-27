package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto City). Behaviour port pending. */
public class AutoCity extends Module {
	private final NumberValue value1 = addValue(new NumberValue("target-range", 5.5, 0.0, 100, 0.1));
	private final NumberValue value2 = addValue(new NumberValue("break-range", 4.5, 0.0, 100, 0.1));
	private final ModeValue value3 = addValue(new ModeValue("switch-mode", "Normal", "Normal", "N", "S"));
	private final BooleanValue value4 = addValue(new BooleanValue("support", true));
	private final NumberValue value5 = addValue(new NumberValue("place-range", 4.5, 0.0, 100, 0.1));
	private final BooleanValue value6 = addValue(new BooleanValue("rotate", true));
	private final BooleanValue value7 = addValue(new BooleanValue("chat-info", true));
	private final BooleanValue value8 = addValue(new BooleanValue("swing-hand", false));
	private final BooleanValue value9 = addValue(new BooleanValue("render-block", true));
	private final ModeValue value10 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public AutoCity() {
		super("Auto City", 0, Category.COMBAT);
	}
}
