package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Time Changer). Behaviour port pending. */
public class TimeChanger extends Module {
	private final NumberValue value1 = addValue(new NumberValue("time", 0.0, -20000.0, 20000.0, 0.1));

	public TimeChanger() {
		super("Time Changer", 0, Category.RENDER);
	}
}
