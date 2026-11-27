package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Bow Aimbot). Behaviour port pending. */
public class BowAimbot extends Module {
	private final NumberValue value1 = addValue(new NumberValue("range", 20.0, 0.0, 100.0, 0.1));
	private final ModeValue value2 = addValue(new ModeValue("priority", "LowestHealth", "LowestHealth", "L", "H", "L", "H", "C"));
	private final BooleanValue value3 = addValue(new BooleanValue("babies", true));
	private final BooleanValue value4 = addValue(new BooleanValue("nametagged", false));
	private final BooleanValue value5 = addValue(new BooleanValue("pause-on-combat", false));

	public BowAimbot() {
		super("Bow Aimbot", 0, Category.COMBAT);
	}
}
