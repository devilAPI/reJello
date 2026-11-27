package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Meteor Client module (Auto Walk). Behaviour port pending. */
public class AutoWalk extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Smart", "Smart", "C", "T", "B"));
	private final ModeValue value2 = addValue(new ModeValue("simple-direction", "Forwards", "Forwards", "R", "L", "T", "B"));
	private final BooleanValue value3 = addValue(new BooleanValue("disable-on-input", false));
	private final BooleanValue value4 = addValue(new BooleanValue("disable-on-y-change", false));
	private final BooleanValue value5 = addValue(new BooleanValue("no-unloaded-chunks", true));

	public AutoWalk() {
		super("Auto Walk", 0, Category.MOVEMENT);
	}
}
