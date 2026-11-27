package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Self Trap). Behaviour port pending. */
public class SelfTrap extends Module {
	private final ModeValue value1 = addValue(new ModeValue("top-mode", "Top", "Top", "A", "F", "T", "N"));
	private final ModeValue value2 = addValue(new ModeValue("bottom-mode", "None", "None", "S", "N"));
	private final NumberValue value3 = addValue(new NumberValue("place-delay", 1, 0, 100, 1));
	private final BooleanValue value4 = addValue(new BooleanValue("center", true));
	private final BooleanValue value5 = addValue(new BooleanValue("turn-off", true));
	private final BooleanValue value6 = addValue(new BooleanValue("rotate", true));
	private final BooleanValue value7 = addValue(new BooleanValue("render", true));
	private final ModeValue value8 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public SelfTrap() {
		super("Self Trap", 0, Category.COMBAT);
	}
}
