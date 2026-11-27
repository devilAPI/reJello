package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Anti Anvil). Behaviour port pending. */
public class AntiAnvil extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("swing", true));
	private final BooleanValue value2 = addValue(new BooleanValue("rotate", true));

	public AntiAnvil() {
		super("Anti Anvil", 0, Category.COMBAT);
	}
}
