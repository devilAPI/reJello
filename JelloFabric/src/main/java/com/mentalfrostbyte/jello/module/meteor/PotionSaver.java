package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Potion Saver). Behaviour port pending. */
public class PotionSaver extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("only-when-stationary", false));

	public PotionSaver() {
		super("Potion Saver", 0, Category.PLAYER);
	}
}
