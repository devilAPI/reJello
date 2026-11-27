package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Anvil). Behaviour port pending. */
public class AutoAnvil extends Module {
	private final NumberValue value1 = addValue(new NumberValue("target-range", 4.0, 0.0, 100, 0.1));
	private final ModeValue value2 = addValue(new ModeValue("target-priority", "LowestHealth", "LowestHealth", "L", "H", "L", "H", "C"));
	private final NumberValue value3 = addValue(new NumberValue("height", 2, 0, 5, 1));
	private final NumberValue value4 = addValue(new NumberValue("delay", 10, 0, 100, 1));
	private final BooleanValue value5 = addValue(new BooleanValue("place-at-feet", true));
	private final BooleanValue value6 = addValue(new BooleanValue("multi-place", true));
	private final BooleanValue value7 = addValue(new BooleanValue("toggle-on-break", false));
	private final BooleanValue value8 = addValue(new BooleanValue("rotate", true));

	public AutoAnvil() {
		super("Auto Anvil", 0, Category.COMBAT);
	}
}
