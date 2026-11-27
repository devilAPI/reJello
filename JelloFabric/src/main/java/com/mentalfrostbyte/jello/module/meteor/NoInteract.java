package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Meteor Client module (No Interact). Behaviour port pending. */
public class NoInteract extends Module {
	private final ModeValue value1 = addValue(new ModeValue("block-mine-mode", "Blacklist", "Blacklist", "W", "B"));
	private final ModeValue value2 = addValue(new ModeValue("block-interact-mode", "Blacklist", "Blacklist", "W", "B"));
	private final ModeValue value3 = addValue(new ModeValue("block-interact-hand", "None", "None", "M", "O", "B", "N"));
	private final ModeValue value4 = addValue(new ModeValue("entity-hit-mode", "Blacklist", "Blacklist", "W", "B"));
	private final ModeValue value5 = addValue(new ModeValue("entity-interact-mode", "Blacklist", "Blacklist", "W", "B"));
	private final ModeValue value6 = addValue(new ModeValue("entity-interact-hand", "None", "None", "M", "O", "B", "N"));
	private final ModeValue value7 = addValue(new ModeValue("friends", "None", "None", "H", "I", "B", "N"));
	private final ModeValue value8 = addValue(new ModeValue("babies", "None", "None", "H", "I", "B", "N"));
	private final ModeValue value9 = addValue(new ModeValue("nametagged", "None", "None", "H", "I", "B", "N"));

	public NoInteract() {
		super("No Interact", 0, Category.PLAYER);
	}
}
