package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Trap). Behaviour port pending. */
public class AutoTrap extends Module {
	private final NumberValue value1 = addValue(new NumberValue("place-range", 4.0, 0.0, 100, 0.1));
	private final NumberValue value2 = addValue(new NumberValue("walls-range", 4.0, 0.0, 100, 0.1));
	private final ModeValue value3 = addValue(new ModeValue("target-priority", "LowestHealth", "LowestHealth", "L", "H", "L", "H", "C"));
	private final NumberValue value4 = addValue(new NumberValue("target-range", 3.0, 0.0, 100, 0.1));
	private final NumberValue value5 = addValue(new NumberValue("place-delay", 1, 0, 100, 1));
	private final NumberValue value6 = addValue(new NumberValue("blocks-per-tick", 1, 1, 100, 1));
	private final ModeValue value7 = addValue(new ModeValue("top-blocks", "Full", "Full", "A", "F", "T", "N"));
	private final ModeValue value8 = addValue(new ModeValue("bottom-blocks", "Platform", "Platform", "S", "N"));
	private final BooleanValue value9 = addValue(new BooleanValue("self-toggle", true));
	private final BooleanValue value10 = addValue(new BooleanValue("rotate", true));
	private final BooleanValue value11 = addValue(new BooleanValue("render", true));
	private final ModeValue value12 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public AutoTrap() {
		super("Auto Trap", 0, Category.COMBAT);
	}
}
