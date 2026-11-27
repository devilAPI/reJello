package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Eat). Behaviour port pending. */
public class AutoEat extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("pause-auras", true));
	private final BooleanValue value2 = addValue(new BooleanValue("pause-baritone", true));
	private final BooleanValue value3 = addValue(new BooleanValue("search-inventory", false));
	private final ModeValue value4 = addValue(new ModeValue("food-priority", "Saturation", "Saturation", "C", "H", "S"));
	private final ModeValue value5 = addValue(new ModeValue("threshold-mode", "Any", "Any", "H", "H", "A", "B"));
	private final NumberValue value6 = addValue(new NumberValue("health-threshold", 10.0, 1.0, 19.0, 0.1));
	private final NumberValue value7 = addValue(new NumberValue("hunger-threshold", 16, 1, 19, 1));

	public AutoEat() {
		super("Auto Eat", 0, Category.PLAYER);
	}
}
