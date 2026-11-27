package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Infinity Miner). Behaviour port pending. */
public class InfinityMiner extends Module {
	private final NumberValue value1 = addValue(new NumberValue("repair-threshold", 20.0, 1.0, 99.0, 0.1));
	private final NumberValue value2 = addValue(new NumberValue("mine-threshold", 70.0, 1.0, 99.0, 0.1));
	private final BooleanValue value3 = addValue(new BooleanValue("walk-home", false));
	private final BooleanValue value4 = addValue(new BooleanValue("log-out", false));

	public InfinityMiner() {
		super("Infinity Miner", 0, Category.WORLD);
	}
}
