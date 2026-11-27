package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Server Spoof). Behaviour port pending. */
public class ServerSpoof extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("spoof-brand", true));
	private final BooleanValue value2 = addValue(new BooleanValue("resource-pack", false));
	private final BooleanValue value3 = addValue(new BooleanValue("block-channels", true));

	public ServerSpoof() {
		super("Server Spoof", 0, Category.MISC);
	}
}
