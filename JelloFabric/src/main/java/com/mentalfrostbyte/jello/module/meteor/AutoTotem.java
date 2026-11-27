package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Totem). Behaviour port pending. */
public class AutoTotem extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Smart", "Smart", "C", "T", "B"));
	private final NumberValue value2 = addValue(new NumberValue("delay", 0, 0, 100, 1));
	private final NumberValue value3 = addValue(new NumberValue("health", 10, 0, 36, 1));
	private final BooleanValue value4 = addValue(new BooleanValue("elytra", true));
	private final BooleanValue value5 = addValue(new BooleanValue("fall", true));
	private final BooleanValue value6 = addValue(new BooleanValue("explosion", true));

	public AutoTotem() {
		super("Auto Totem", 0, Category.COMBAT);
	}
}
