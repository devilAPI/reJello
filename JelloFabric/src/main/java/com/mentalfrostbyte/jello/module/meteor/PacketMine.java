package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Packet Mine). Behaviour port pending. */
public class PacketMine extends Module {
	private final NumberValue value1 = addValue(new NumberValue("delay", 1, 0, 100, 1));
	private final BooleanValue value2 = addValue(new BooleanValue("rotate", true));
	private final BooleanValue value3 = addValue(new BooleanValue("auto-switch", false));
	private final BooleanValue value4 = addValue(new BooleanValue("not-on-use", true));
	private final BooleanValue value5 = addValue(new BooleanValue("obscure-breaking-progress", false));
	private final BooleanValue value6 = addValue(new BooleanValue("render", true));
	private final ModeValue value7 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public PacketMine() {
		super("Packet Mine", 0, Category.WORLD);
	}
}
