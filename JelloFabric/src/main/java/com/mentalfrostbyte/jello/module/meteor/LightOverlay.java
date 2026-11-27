package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Light Overlay). Behaviour port pending. */
public class LightOverlay extends Module {
	private final NumberValue value1 = addValue(new NumberValue("horizontal-range", 8, 0, 100, 1));
	private final NumberValue value2 = addValue(new NumberValue("vertical-range", 4, 0, 100, 1));
	private final BooleanValue value3 = addValue(new BooleanValue("see-through-blocks", false));
	private final NumberValue value4 = addValue(new NumberValue("light-level", 0, 0, 100, 1));

	public LightOverlay() {
		super("Light Overlay", 0, Category.RENDER);
	}
}
