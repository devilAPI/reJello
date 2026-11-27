package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Meteor Client module (Block Selection). Behaviour port pending. */
public class BlockSelection extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("advanced", true));
	private final BooleanValue value2 = addValue(new BooleanValue("single-side", false));
	private final ModeValue value3 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));
	private final BooleanValue value4 = addValue(new BooleanValue("hide-when-inside-block", true));

	public BlockSelection() {
		super("Block Selection", 0, Category.RENDER);
	}
}
