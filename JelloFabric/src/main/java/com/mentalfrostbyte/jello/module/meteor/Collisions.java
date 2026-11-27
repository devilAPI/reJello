package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Collisions). Behaviour port pending. */
public class Collisions extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("magma", false));
	private final BooleanValue value2 = addValue(new BooleanValue("unloaded-chunks", false));
	private final BooleanValue value3 = addValue(new BooleanValue("ignore-border", false));

	public Collisions() {
		super("Collisions", 0, Category.WORLD);
	}
}
