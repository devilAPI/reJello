package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Liquid Filler). Behaviour port pending. */
public class LiquidFiller extends Module {
	private final ModeValue value1 = addValue(new ModeValue("place-in", "Both", "Both", "B", "W", "L"));
	private final ModeValue value2 = addValue(new ModeValue("shape", "Sphere", "Sphere", "C", "U", "S"));
	private final NumberValue value3 = addValue(new NumberValue("place-range", 4.5, 0.0, 100, 0.1));
	private final NumberValue value4 = addValue(new NumberValue("walls-range", 4.5, 0.0, 100, 0.1));
	private final NumberValue value5 = addValue(new NumberValue("delay", 0, 0, 100, 1));
	private final NumberValue value6 = addValue(new NumberValue("max-blocks-per-tick", 1, 1, 10, 1));
	private final ModeValue value7 = addValue(new ModeValue("sort-mode", "Furthest", "Furthest", "N", "C", "F", "T"));
	private final BooleanValue value8 = addValue(new BooleanValue("rotate", true));
	private final ModeValue value9 = addValue(new ModeValue("list-mode", "Whitelist", "Whitelist", "W", "B"));

	public LiquidFiller() {
		super("Liquid Filler", 0, Category.WORLD);
	}
}
