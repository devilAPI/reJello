package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Meteor Client module (Excavator). Behaviour port pending. */
public class Excavator extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("log-selection", true));
	private final BooleanValue value2 = addValue(new BooleanValue("keep-active", false));
	private final ModeValue value3 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public Excavator() {
		super("Excavator", 0, Category.WORLD);
	}
}
