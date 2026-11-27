package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (No Ghost Blocks). Behaviour port pending. */
public class NoGhostBlocks extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("breaking", true));
	private final BooleanValue value2 = addValue(new BooleanValue("placing", true));

	public NoGhostBlocks() {
		super("No Ghost Blocks", 0, Category.WORLD);
	}
}
