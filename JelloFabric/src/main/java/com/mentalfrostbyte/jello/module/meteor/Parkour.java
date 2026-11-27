package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Parkour). Behaviour port pending. */
public class Parkour extends Module {
	private final NumberValue value1 = addValue(new NumberValue("edge-distance", 0.001, 0.001, 0.1, 0.1));

	public Parkour() {
		super("Parkour", 0, Category.MOVEMENT);
	}
}
