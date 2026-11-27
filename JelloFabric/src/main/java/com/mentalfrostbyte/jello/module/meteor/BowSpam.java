package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Bow Spam). Behaviour port pending. */
public class BowSpam extends Module {
	private final NumberValue value1 = addValue(new NumberValue("charge", 5, 4, 20, 1));
	private final BooleanValue value2 = addValue(new BooleanValue("when-holding-right-click", false));
	private final BooleanValue value3 = addValue(new BooleanValue("spam-crossbows", true));
	private final NumberValue value4 = addValue(new NumberValue("crossbow-delay", 10, 0, 20, 1));
	private final BooleanValue value5 = addValue(new BooleanValue("search-inventory", true));

	public BowSpam() {
		super("Bow Spam", 0, Category.COMBAT);
	}
}
