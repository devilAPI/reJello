package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Hole Filler). Behaviour port pending. */
public class HoleFiller extends Module {
	private final NumberValue value1 = addValue(new NumberValue("search-radius", 5, 0, 100, 1));
	private final NumberValue value2 = addValue(new NumberValue("place-range", 4.5, 0.0, 100, 0.1));
	private final NumberValue value3 = addValue(new NumberValue("walls-range", 4.5, 0.0, 100, 0.1));
	private final BooleanValue value4 = addValue(new BooleanValue("doubles", true));
	private final BooleanValue value5 = addValue(new BooleanValue("rotate", false));
	private final NumberValue value6 = addValue(new NumberValue("place-delay", 1, 0, 100, 1));
	private final NumberValue value7 = addValue(new NumberValue("blocks-per-tick", 3, 1, 100, 1));
	private final BooleanValue value8 = addValue(new BooleanValue("smart", true));
	private final BooleanValue value9 = addValue(new BooleanValue("predict-movement", true));
	private final NumberValue value10 = addValue(new NumberValue("ticks-to-predict", 10.0, 1.0, 100, 0.1));
	private final BooleanValue value11 = addValue(new BooleanValue("ignore-safe", true));
	private final BooleanValue value12 = addValue(new BooleanValue("only-moving", true));
	private final NumberValue value13 = addValue(new NumberValue("target-range", 7.0, 0.0, 100, 0.1));
	private final NumberValue value14 = addValue(new NumberValue("feet-range", 1.5, 0.0, 100, 0.1));
	private final BooleanValue value15 = addValue(new BooleanValue("swing", true));
	private final BooleanValue value16 = addValue(new BooleanValue("render", true));
	private final ModeValue value17 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public HoleFiller() {
		super("Hole Filler", 0, Category.COMBAT);
	}
}
