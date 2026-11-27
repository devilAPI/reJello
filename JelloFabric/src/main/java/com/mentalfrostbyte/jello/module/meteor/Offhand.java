package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Offhand). Behaviour port pending. */
public class Offhand extends Module {
	private final NumberValue value1 = addValue(new NumberValue("item-switch-delay", 0, 0, 100, 1));
	private final ModeValue value2 = addValue(new ModeValue("item", "Crystal", "Crystal", "EG", "G", "C", "T", "S", "P"));
	private final BooleanValue value3 = addValue(new BooleanValue("hotbar", false));
	private final BooleanValue value4 = addValue(new BooleanValue("right-gapple", false));
	private final BooleanValue value5 = addValue(new BooleanValue("sword-gapple", false));
	private final BooleanValue value6 = addValue(new BooleanValue("always-gap-on-sword", false));
	private final BooleanValue value7 = addValue(new BooleanValue("always-pot-on-sword", false));
	private final BooleanValue value8 = addValue(new BooleanValue("sword-pot", false));
	private final NumberValue value9 = addValue(new NumberValue("min-health", 10.0, 0.0, 36.0, 0.1));
	private final BooleanValue value10 = addValue(new BooleanValue("elytra", false));
	private final BooleanValue value11 = addValue(new BooleanValue("falling", false));
	private final BooleanValue value12 = addValue(new BooleanValue("explosion", true));

	public Offhand() {
		super("Offhand", 0, Category.COMBAT);
	}
}
