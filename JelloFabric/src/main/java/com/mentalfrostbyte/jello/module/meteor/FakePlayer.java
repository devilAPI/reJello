package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Fake Player). Behaviour port pending. */
public class FakePlayer extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("copy-inv", true));
	private final NumberValue value2 = addValue(new NumberValue("health", 20, 1, 100, 1));

	public FakePlayer() {
		super("Fake Player", 0, Category.PLAYER);
	}
}
