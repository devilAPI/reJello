package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (No Mining Trace). Behaviour port pending. */
public class NoMiningTrace extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("only-when-holding-a-pickaxe", true));

	public NoMiningTrace() {
		super("No Mining Trace", 0, Category.PLAYER);
	}
}
