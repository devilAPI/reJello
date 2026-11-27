package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Better Tab). Behaviour port pending. */
public class BetterTab extends Module {
	private final NumberValue value1 = addValue(new NumberValue("tablist-size", 100, 1, 1000, 1));
	private final NumberValue value2 = addValue(new NumberValue("column-height", 20, 1, 1000, 1));
	private final BooleanValue value3 = addValue(new BooleanValue("highlight-self", true));
	private final BooleanValue value4 = addValue(new BooleanValue("highlight-friends", true));
	private final BooleanValue value5 = addValue(new BooleanValue("accurate-latency", true));
	private final BooleanValue value6 = addValue(new BooleanValue("gamemode", false));

	public BetterTab() {
		super("Better Tab", 0, Category.RENDER);
	}
}
