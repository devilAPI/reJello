package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Swarm). Behaviour port pending. */
public class Swarm extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Host", "Host", "C", "T", "B"));
	private final NumberValue value2 = addValue(new NumberValue("port", 6969, 1, 65535, 1));

	public Swarm() {
		super("Swarm", 0, Category.MISC);
	}
}
