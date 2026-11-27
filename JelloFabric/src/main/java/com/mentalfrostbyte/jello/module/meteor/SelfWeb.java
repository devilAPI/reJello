package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Self Web). Behaviour port pending. */
public class SelfWeb extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Normal", "Normal", "C", "T", "B"));
	private final NumberValue value2 = addValue(new NumberValue("range", 3, 1, 7, 1));
	private final BooleanValue value3 = addValue(new BooleanValue("double-place", false));
	private final BooleanValue value4 = addValue(new BooleanValue("auto-toggle", true));
	private final BooleanValue value5 = addValue(new BooleanValue("rotate", true));

	public SelfWeb() {
		super("Self Web", 0, Category.COMBAT);
	}
}
