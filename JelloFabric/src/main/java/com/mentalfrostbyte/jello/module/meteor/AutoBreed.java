package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Breed). Behaviour port pending. */
public class AutoBreed extends Module {
	private final NumberValue value1 = addValue(new NumberValue("range", 4.5, 0.0, 100, 0.1));
	private final ModeValue value2 = addValue(new ModeValue("hand-for-breeding", "MAIN_HAND", "MAIN_HAND"));
	private final ModeValue value3 = addValue(new ModeValue("mob-age-filter", "Adult", "Adult", "B", "A", "B"));
	private final BooleanValue value4 = addValue(new BooleanValue("continuous-breeding", false));
	private final NumberValue value5 = addValue(new NumberValue("breeding-interval", 100, 1, 100, 1));

	public AutoBreed() {
		super("Auto Breed", 0, Category.WORLD);
	}
}
