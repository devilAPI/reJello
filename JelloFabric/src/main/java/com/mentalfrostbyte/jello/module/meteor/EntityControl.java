package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Entity Control). Behaviour port pending. */
public class EntityControl extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("spoof-saddle*", false));
	private final BooleanValue value2 = addValue(new BooleanValue("max-jump", true));
	private final BooleanValue value3 = addValue(new BooleanValue("lock-yaw", true));
	private final BooleanValue value4 = addValue(new BooleanValue("cancel-server-packets", false));
	private final BooleanValue value5 = addValue(new BooleanValue("speed", false));
	private final NumberValue value6 = addValue(new NumberValue("horizontal-speed", 10.0, 0.0, 100, 0.1));
	private final BooleanValue value7 = addValue(new BooleanValue("only-on-ground", false));
	private final BooleanValue value8 = addValue(new BooleanValue("in-water", true));
	private final BooleanValue value9 = addValue(new BooleanValue("fly", false));
	private final NumberValue value10 = addValue(new NumberValue("vertical-speed", 6.0, 0.0, 100, 0.1));
	private final NumberValue value11 = addValue(new NumberValue("fall-speed", 0.0, 0.0, 100, 0.1));
	private final BooleanValue value12 = addValue(new BooleanValue("anti-fly-kick", true));
	private final NumberValue value13 = addValue(new NumberValue("delay", 40, 1, 100, 1));

	public EntityControl() {
		super("Entity Control", 0, Category.MOVEMENT);
	}
}
