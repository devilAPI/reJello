package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Block Esp). Behaviour port pending. */
public class BlockESP extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("tracers", false));

	public BlockESP() {
		super("Block Esp", 0, Category.RENDER);
	}
}
