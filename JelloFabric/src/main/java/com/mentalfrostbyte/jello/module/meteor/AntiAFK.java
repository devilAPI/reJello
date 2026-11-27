package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Anti Afk). Behaviour port pending. */
public class AntiAFK extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("jump", true));
	private final BooleanValue value2 = addValue(new BooleanValue("swing", false));
	private final BooleanValue value3 = addValue(new BooleanValue("sneak", false));
	private final NumberValue value4 = addValue(new NumberValue("sneak-time", 5, 1, 100, 1));
	private final BooleanValue value5 = addValue(new BooleanValue("strafe", false));
	private final BooleanValue value6 = addValue(new BooleanValue("spin", true));
	private final ModeValue value7 = addValue(new ModeValue("spin-mode", "Server", "Server", "S", "C"));
	private final NumberValue value8 = addValue(new NumberValue("speed", 7, 0, 100, 1));
	private final NumberValue value9 = addValue(new NumberValue("pitch", 0, -90, 90, 1));
	private final BooleanValue value10 = addValue(new BooleanValue("send-messages", false));
	private final BooleanValue value11 = addValue(new BooleanValue("random", false));
	private final NumberValue value12 = addValue(new NumberValue("delay", 15, 0, 100, 1));

	public AntiAFK() {
		super("Anti Afk", 0, Category.PLAYER);
	}
}
