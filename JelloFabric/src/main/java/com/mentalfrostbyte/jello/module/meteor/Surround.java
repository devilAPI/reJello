package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Surround). Behaviour port pending. */
public class Surround extends Module {
	private final NumberValue value1 = addValue(new NumberValue("delay", 0, 0, 100, 1));
	private final NumberValue value2 = addValue(new NumberValue("blocks-per-tick", 1, 1, 100, 1));
	private final ModeValue value3 = addValue(new ModeValue("center", "Incomplete", "Incomplete", "N", "O", "I", "A"));
	private final BooleanValue value4 = addValue(new BooleanValue("double-height", false));
	private final BooleanValue value5 = addValue(new BooleanValue("only-on-ground", true));
	private final BooleanValue value6 = addValue(new BooleanValue("air-place", true));
	private final BooleanValue value7 = addValue(new BooleanValue("toggle-modules", false));
	private final BooleanValue value8 = addValue(new BooleanValue("toggle-back-on", false));
	private final BooleanValue value9 = addValue(new BooleanValue("rotate", true));
	private final BooleanValue value10 = addValue(new BooleanValue("protect", true));
	private final BooleanValue value11 = addValue(new BooleanValue("toggle-on-y-change", true));
	private final BooleanValue value12 = addValue(new BooleanValue("toggle-on-complete", false));
	private final BooleanValue value13 = addValue(new BooleanValue("toggle-on-death", true));
	private final BooleanValue value14 = addValue(new BooleanValue("swing", true));
	private final BooleanValue value15 = addValue(new BooleanValue("render", true));
	private final BooleanValue value16 = addValue(new BooleanValue("below", false));
	private final ModeValue value17 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public Surround() {
		super("Surround", 0, Category.COMBAT);
	}
}
