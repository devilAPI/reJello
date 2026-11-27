package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Log). Behaviour port pending. */
public class AutoLog extends Module {
	private final NumberValue value1 = addValue(new NumberValue("health", 6, 0, 19, 1));
	private final BooleanValue value2 = addValue(new BooleanValue("predict-incoming-damage", true));
	private final NumberValue value3 = addValue(new NumberValue("totem-pops", 0, 0, 100, 1));
	private final BooleanValue value4 = addValue(new BooleanValue("only-trusted", false));
	private final BooleanValue value5 = addValue(new BooleanValue("32K", false));
	private final BooleanValue value6 = addValue(new BooleanValue("smart-toggle", false));
	private final BooleanValue value7 = addValue(new BooleanValue("toggle-off", true));
	private final BooleanValue value8 = addValue(new BooleanValue("toggle-auto-reconnect", true));
	private final BooleanValue value9 = addValue(new BooleanValue("use-total-count", true));
	private final NumberValue value10 = addValue(new NumberValue("combined-entity-threshold", 10, 1, 100, 1));
	private final NumberValue value11 = addValue(new NumberValue("individual-entity-threshold", 2, 1, 100, 1));
	private final NumberValue value12 = addValue(new NumberValue("range", 5, 1, 100, 1));

	public AutoLog() {
		super("Auto Log", 0, Category.COMBAT);
	}
}
