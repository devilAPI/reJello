package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Discord Presence). Behaviour port pending. */
public class DiscordPresence extends Module {
	private final NumberValue value1 = addValue(new NumberValue("line-1-update-delay", 200, 10, 200, 1));
	private final ModeValue value2 = addValue(new ModeValue("line-1-select-mode", "Sequential", "Sequential", "R", "S"));
	private final NumberValue value3 = addValue(new NumberValue("line-2-update-delay", 60, 10, 200, 1));
	private final ModeValue value4 = addValue(new ModeValue("line-2-select-mode", "Sequential", "Sequential", "R", "S"));

	public DiscordPresence() {
		super("Discord Presence", 0, Category.MISC);
	}
}
