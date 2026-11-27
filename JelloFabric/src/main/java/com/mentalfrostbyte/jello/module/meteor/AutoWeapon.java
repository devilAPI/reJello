package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Weapon). Behaviour port pending. */
public class AutoWeapon extends Module {
	private final ModeValue value1 = addValue(new ModeValue("weapon", "Sword", "Sword", "S", "A"));
	private final NumberValue value2 = addValue(new NumberValue("threshold", 4, 0, 100, 1));
	private final BooleanValue value3 = addValue(new BooleanValue("anti-break", false));

	public AutoWeapon() {
		super("Auto Weapon", 0, Category.COMBAT);
	}
}
