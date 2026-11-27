package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Quiver). Behaviour port pending. */
public class Quiver extends Module {
	private final NumberValue value1 = addValue(new NumberValue("cooldown", 10, 0, 40, 1));
	private final BooleanValue value2 = addValue(new BooleanValue("check-effects", true));
	private final BooleanValue value3 = addValue(new BooleanValue("silent-bow", true));
	private final BooleanValue value4 = addValue(new BooleanValue("chat-info", false));
	private final BooleanValue value5 = addValue(new BooleanValue("only-in-holes", true));
	private final BooleanValue value6 = addValue(new BooleanValue("only-on-ground", true));
	private final NumberValue value7 = addValue(new NumberValue("min-health", 10.0, 0.0, 36.0, 0.1));

	public Quiver() {
		super("Quiver", 0, Category.COMBAT);
	}
}
