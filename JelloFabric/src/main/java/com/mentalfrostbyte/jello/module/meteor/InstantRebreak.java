package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Instant Rebreak). Behaviour port pending. */
public class InstantRebreak extends Module {
	private final NumberValue value1 = addValue(new NumberValue("delay", 0, 0, 100, 1));
	private final BooleanValue value2 = addValue(new BooleanValue("only-pick", true));
	private final BooleanValue value3 = addValue(new BooleanValue("rotate", true));
	private final BooleanValue value4 = addValue(new BooleanValue("render", true));
	private final ModeValue value5 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public InstantRebreak() {
		super("Instant Rebreak", 0, Category.PLAYER);
	}
}
