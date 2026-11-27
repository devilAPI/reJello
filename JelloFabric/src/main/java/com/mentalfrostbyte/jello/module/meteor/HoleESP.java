package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Hole Esp). Behaviour port pending. */
public class HoleESP extends Module {
	private final NumberValue value1 = addValue(new NumberValue("horizontal-radius", 10, 0, 100, 1));
	private final NumberValue value2 = addValue(new NumberValue("vertical-radius", 5, 0, 100, 1));
	private final NumberValue value3 = addValue(new NumberValue("min-height", 3, 1, 100, 1));
	private final BooleanValue value4 = addValue(new BooleanValue("doubles", true));
	private final BooleanValue value5 = addValue(new BooleanValue("ignore-own", false));
	private final BooleanValue value6 = addValue(new BooleanValue("webs", false));
	private final ModeValue value7 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));
	private final NumberValue value8 = addValue(new NumberValue("height", 0.2, 0.0, 100, 0.1));
	private final BooleanValue value9 = addValue(new BooleanValue("top-quad", true));
	private final BooleanValue value10 = addValue(new BooleanValue("bottom-quad", false));

	public HoleESP() {
		super("Hole Esp", 0, Category.RENDER);
	}
}
