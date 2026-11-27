package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Trident Boost). Behaviour port pending. */
public class TridentBoost extends Module {
	private final NumberValue value1 = addValue(new NumberValue("boost", 2.0, 0.1, 100, 0.1));
	private final BooleanValue value2 = addValue(new BooleanValue("out-of-water", true));

	public TridentBoost() {
		super("Trident Boost", 0, Category.MOVEMENT);
	}
}
