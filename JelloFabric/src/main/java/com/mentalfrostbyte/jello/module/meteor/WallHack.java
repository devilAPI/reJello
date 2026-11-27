package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Wall Hack). Behaviour port pending. */
public class WallHack extends Module {
	private final NumberValue value1 = addValue(new NumberValue("opacity", 0, 0, 255, 1));
	private final BooleanValue value2 = addValue(new BooleanValue("occlude-chunks", false));

	public WallHack() {
		super("Wall Hack", 0, Category.RENDER);
	}
}
