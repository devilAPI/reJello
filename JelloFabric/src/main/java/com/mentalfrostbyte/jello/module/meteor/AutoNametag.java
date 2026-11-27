package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Nametag). Behaviour port pending. */
public class AutoNametag extends Module {
	private final NumberValue value1 = addValue(new NumberValue("range", 5.0, 0.0, 100, 0.1));
	private final ModeValue value2 = addValue(new ModeValue("priority", "LowestDistance", "LowestDistance", "L", "H", "L", "H", "C"));
	private final BooleanValue value3 = addValue(new BooleanValue("renametag", true));
	private final BooleanValue value4 = addValue(new BooleanValue("rotate", true));

	public AutoNametag() {
		super("Auto Nametag", 0, Category.WORLD);
	}
}
