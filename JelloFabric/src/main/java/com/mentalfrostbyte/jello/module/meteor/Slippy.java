package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Slippy). Behaviour port pending. */
public class Slippy extends Module {
	private final NumberValue value1 = addValue(new NumberValue("friction", 1.0, 0.01, 1.1, 0.1));
	private final ModeValue value2 = addValue(new ModeValue("list-mode", "Blacklist", "Blacklist", "W", "B"));

	public Slippy() {
		super("Slippy", 0, Category.MOVEMENT);
	}
}
