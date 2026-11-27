package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Pop Chams). Behaviour port pending. */
public class PopChams extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("only-one", false));
	private final NumberValue value2 = addValue(new NumberValue("render-time", 1.0, 0.1, 100, 0.1));
	private final NumberValue value3 = addValue(new NumberValue("y-modifier", 0.75, -4.0, 4.0, 0.1));
	private final NumberValue value4 = addValue(new NumberValue("scale-modifier", -0.25, -4.0, 4.0, 0.1));
	private final BooleanValue value5 = addValue(new BooleanValue("fade-out", true));
	private final ModeValue value6 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public PopChams() {
		super("Pop Chams", 0, Category.RENDER);
	}
}
