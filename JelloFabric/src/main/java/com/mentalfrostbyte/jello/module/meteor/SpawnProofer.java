package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Spawn Proofer). Behaviour port pending. */
public class SpawnProofer extends Module {
	private final NumberValue value1 = addValue(new NumberValue("place-delay", 1, 0, 10, 1));
	private final NumberValue value2 = addValue(new NumberValue("place-range", 4.5, 0.0, 100, 0.1));
	private final NumberValue value3 = addValue(new NumberValue("walls-range", 4.5, 0.0, 100, 0.1));
	private final NumberValue value4 = addValue(new NumberValue("blocks-per-tick", 1, 1, 100, 1));
	private final NumberValue value5 = addValue(new NumberValue("light-level", 0, 0, 100, 1));
	private final ModeValue value6 = addValue(new ModeValue("mode", "Both", "Both", "C", "T", "B"));
	private final BooleanValue value7 = addValue(new BooleanValue("rotate", true));

	public SpawnProofer() {
		super("Spawn Proofer", 0, Category.WORLD);
	}
}
