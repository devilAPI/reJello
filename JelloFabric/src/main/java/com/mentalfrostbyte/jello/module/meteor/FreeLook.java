package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Free Look). Behaviour port pending. */
public class FreeLook extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Player", "Player", "C", "T", "B"));
	private final BooleanValue value2 = addValue(new BooleanValue("toggle-perspective", true));
	private final NumberValue value3 = addValue(new NumberValue("camera-sensitivity", 8.0, 0.0, 100, 0.1));
	private final BooleanValue value4 = addValue(new BooleanValue("arrows-control-opposite", true));
	private final NumberValue value5 = addValue(new NumberValue("arrow-speed", 4.0, 0.0, 100, 0.1));

	public FreeLook() {
		super("Free Look", 0, Category.RENDER);
	}
}
