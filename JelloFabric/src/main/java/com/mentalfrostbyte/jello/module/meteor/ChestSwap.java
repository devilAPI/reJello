package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Meteor Client module (Chest Swap). Behaviour port pending. */
public class ChestSwap extends Module {
	private final ModeValue value1 = addValue(new ModeValue("chestplate", "PreferNetherite", "PreferNetherite", "D", "N", "P", "P"));
	private final BooleanValue value2 = addValue(new BooleanValue("stay-on", false));
	private final BooleanValue value3 = addValue(new BooleanValue("close-inventory", true));

	public ChestSwap() {
		super("Chest Swap", 0, Category.PLAYER);
	}
}
