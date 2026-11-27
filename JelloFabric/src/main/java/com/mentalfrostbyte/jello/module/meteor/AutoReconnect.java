package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Auto Reconnect). Behaviour port pending. */
public class AutoReconnect extends Module {
	private final NumberValue value1 = addValue(new NumberValue("delay", 3.5, 0.0, 100, 0.1));
	private final BooleanValue value2 = addValue(new BooleanValue("hide-buttons", false));

	public AutoReconnect() {
		super("Auto Reconnect", 0, Category.MISC);
	}
}
