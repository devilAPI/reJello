package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Burrow). Behaviour port pending. */
public class Burrow extends Module {
	private final ModeValue value1 = addValue(new ModeValue("block-to-use", "EChest", "EChest", "EC", "O", "A", "H"));
	private final BooleanValue value2 = addValue(new BooleanValue("instant", true));
	private final BooleanValue value3 = addValue(new BooleanValue("automatic", true));
	private final NumberValue value4 = addValue(new NumberValue("trigger-height", 1.12, 0.01, 1.4, 0.1));
	private final NumberValue value5 = addValue(new NumberValue("rubberband-height", 12.0, 0, 100, 0.1));
	private final NumberValue value6 = addValue(new NumberValue("timer", 1.0, 0.01, 10.0, 0.1));
	private final BooleanValue value7 = addValue(new BooleanValue("only-in-holes", false));
	private final BooleanValue value8 = addValue(new BooleanValue("center", true));
	private final BooleanValue value9 = addValue(new BooleanValue("rotate", true));

	public Burrow() {
		super("Burrow", 0, Category.COMBAT);
	}
}
