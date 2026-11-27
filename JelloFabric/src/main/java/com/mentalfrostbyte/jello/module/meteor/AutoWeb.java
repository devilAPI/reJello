package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Web). Behaviour port pending. */
public class AutoWeb extends Module {
	private final NumberValue value1 = addValue(new NumberValue("place-range", 4.0, 0.0, 100, 0.1));
	private final NumberValue value2 = addValue(new NumberValue("walls-range", 4.0, 0.0, 100, 0.1));
	private final ModeValue value3 = addValue(new ModeValue("target-priority", "LowestDistance", "LowestDistance", "L", "H", "L", "H", "C"));
	private final NumberValue value4 = addValue(new NumberValue("target-range", 10.0, 0.0, 100, 0.1));
	private final BooleanValue value5 = addValue(new BooleanValue("predict-movement", true));
	private final NumberValue value6 = addValue(new NumberValue("ticks-to-predict", 10.0, 1.0, 100, 0.1));
	private final BooleanValue value7 = addValue(new BooleanValue("doubles", false));
	private final BooleanValue value8 = addValue(new BooleanValue("rotate", true));
	private final BooleanValue value9 = addValue(new BooleanValue("render", true));
	private final ModeValue value10 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public AutoWeb() {
		super("Auto Web", 0, Category.COMBAT);
	}
}
