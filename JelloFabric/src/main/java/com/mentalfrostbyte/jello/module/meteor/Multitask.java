package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Multitask). Behaviour port pending. */
public class Multitask extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("attacking-entities", true));

	public Multitask() {
		super("Multitask", 0, Category.PLAYER);
	}
}
