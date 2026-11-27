package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Spam). Behaviour port pending. */
public class Spam extends Module {
	private final NumberValue value1 = addValue(new NumberValue("delay", 20, 0, 100, 1));
	private final BooleanValue value2 = addValue(new BooleanValue("disable-on-leave", true));
	private final BooleanValue value3 = addValue(new BooleanValue("disable-on-disconnect", true));
	private final BooleanValue value4 = addValue(new BooleanValue("randomise", false));
	private final BooleanValue value5 = addValue(new BooleanValue("auto-split-messages", false));
	private final NumberValue value6 = addValue(new NumberValue("split-length", 100, 1, 100, 1));
	private final NumberValue value7 = addValue(new NumberValue("split-delay", 20, 0, 100, 1));
	private final BooleanValue value8 = addValue(new BooleanValue("bypass", false));
	private final BooleanValue value9 = addValue(new BooleanValue("include-uppercase-characters", true));
	private final NumberValue value10 = addValue(new NumberValue("length", 16, 1, 256, 1));

	public Spam() {
		super("Spam", 0, Category.MISC);
	}
}
