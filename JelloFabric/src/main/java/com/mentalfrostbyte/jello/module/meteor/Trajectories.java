package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Trajectories). Behaviour port pending. */
public class Trajectories extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("other-players", true));
	private final BooleanValue value2 = addValue(new BooleanValue("fired-projectiles", false));
	private final BooleanValue value3 = addValue(new BooleanValue("ignore-wither-skulls", false));
	private final BooleanValue value4 = addValue(new BooleanValue("accurate", false));
	private final NumberValue value5 = addValue(new NumberValue("simulation-steps", 100, 0, 100, 1));
	private final NumberValue value6 = addValue(new NumberValue("ignore-rendering-first-ticks", 3, 0, 100, 1));
	private final ModeValue value7 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));
	private final BooleanValue value8 = addValue(new BooleanValue("render-position-boxes", false));
	private final NumberValue value9 = addValue(new NumberValue("position-box-size", 0.02, 0.01, 0.1, 0.1));

	public Trajectories() {
		super("Trajectories", 0, Category.RENDER);
	}
}
