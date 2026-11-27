package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Elytra Boost). Behaviour port pending. */
public class ElytraBoost extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("anti-consume", true));
	private final NumberValue value2 = addValue(new NumberValue("firework-duration", 0, 0, 255, 1));
	private final BooleanValue value3 = addValue(new BooleanValue("play-sound", true));

	public ElytraBoost() {
		super("Elytra Boost", 0, Category.MOVEMENT);
	}
}
