package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Meteor Client module (Enderman Look). Behaviour port pending. */
public class EndermanLook extends Module {
	private final ModeValue value1 = addValue(new ModeValue("look-mode", "Away", "Away", "C", "T", "B"));
	private final BooleanValue value2 = addValue(new BooleanValue("stun-hostiles", true));

	public EndermanLook() {
		super("Enderman Look", 0, Category.WORLD);
	}
}
