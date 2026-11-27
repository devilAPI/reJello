package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Meteor Client module (City Esp). Behaviour port pending. */
public class CityESP extends Module {
	private final ModeValue value1 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public CityESP() {
		super("City Esp", 0, Category.RENDER);
	}
}
