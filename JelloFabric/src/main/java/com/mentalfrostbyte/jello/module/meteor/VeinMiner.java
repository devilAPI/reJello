package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Vein Miner). Behaviour port pending. */
public class VeinMiner extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Blacklist", "Blacklist", "W", "B"));
	private final NumberValue value2 = addValue(new NumberValue("depth", 3, 1, 15, 1));
	private final NumberValue value3 = addValue(new NumberValue("delay", 0, 0, 20, 1));
	private final BooleanValue value4 = addValue(new BooleanValue("rotate", true));
	private final BooleanValue value5 = addValue(new BooleanValue("swing-hand", true));
	private final BooleanValue value6 = addValue(new BooleanValue("render", true));
	private final ModeValue value7 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public VeinMiner() {
		super("Vein Miner", 0, Category.WORLD);
	}
}
