package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Rotation). Behaviour port pending. */
public class Rotation extends Module {
	private final ModeValue value1 = addValue(new ModeValue("yaw-lock-mode", "Simple", "Simple", "S", "S", "N"));
	private final NumberValue value2 = addValue(new NumberValue("yaw-angle", 0, 0, 360.0, 0.1));
	private final ModeValue value3 = addValue(new ModeValue("pitch-lock-mode", "Simple", "Simple", "S", "S", "N"));
	private final NumberValue value4 = addValue(new NumberValue("pitch-angle", 0.0, -90.0, 90.0, 0.1));

	public Rotation() {
		super("Rotation", 0, Category.PLAYER);
	}
}
