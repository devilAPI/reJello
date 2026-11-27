package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Hand View). Behaviour port pending. */
public class HandView extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("server-rotations", false));
	private final BooleanValue value2 = addValue(new BooleanValue("old-animations", false));
	private final BooleanValue value3 = addValue(new BooleanValue("skip-swapping-animation", false));
	private final BooleanValue value4 = addValue(new BooleanValue("disable-eating-animation", false));
	private final BooleanValue value5 = addValue(new BooleanValue("sword-slash", false));
	private final ModeValue value6 = addValue(new ModeValue("swing-mode", "None", "None", "B", "P", "C", "N"));
	private final NumberValue value7 = addValue(new NumberValue("swing-speed", 6.0, 0.0, 20.0, 0.1));
	private final NumberValue value8 = addValue(new NumberValue("main-hand-progress", 0.0, 0.0, 1.0, 0.1));
	private final NumberValue value9 = addValue(new NumberValue("off-hand-progress", 0.0, 0.0, 1.0, 0.1));

	public HandView() {
		super("Hand View", 0, Category.RENDER);
	}
}
