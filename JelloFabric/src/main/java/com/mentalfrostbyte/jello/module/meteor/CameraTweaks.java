package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Camera Tweaks). Behaviour port pending. */
public class CameraTweaks extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("clip", true));
	private final NumberValue value2 = addValue(new NumberValue("camera-distance", 4.0, 0.0, 100, 0.1));
	private final BooleanValue value3 = addValue(new BooleanValue("scrolling", true));
	private final NumberValue value4 = addValue(new NumberValue("sensitivity", 1.0, 0.01, 100, 0.1));

	public CameraTweaks() {
		super("Camera Tweaks", 0, Category.RENDER);
	}
}
