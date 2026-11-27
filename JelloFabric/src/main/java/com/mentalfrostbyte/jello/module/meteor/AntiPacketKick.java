package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Anti Packet Kick). Behaviour port pending. */
public class AntiPacketKick extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("catch-exceptions", false));
	private final BooleanValue value2 = addValue(new BooleanValue("log-exceptions", true));

	public AntiPacketKick() {
		super("Anti Packet Kick", 0, Category.MISC);
	}
}
