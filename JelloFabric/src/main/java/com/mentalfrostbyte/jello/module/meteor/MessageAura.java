package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Message Aura). Behaviour port pending. */
public class MessageAura extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("ignore-friends", false));

	public MessageAura() {
		super("Message Aura", 0, Category.MISC);
	}
}
