package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Void Esp). Behaviour port pending. */
public class VoidESP extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("air-only", false));
	private final NumberValue value2 = addValue(new NumberValue("horizontal-radius", 64, 0, 100, 1));
	private final NumberValue value3 = addValue(new NumberValue("hole-height", 1, 1, 5, 1));
	private final BooleanValue value4 = addValue(new BooleanValue("nether-roof", true));
	private final ModeValue value5 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public VoidESP() {
		super("Void Esp", 0, Category.RENDER);
	}
}
