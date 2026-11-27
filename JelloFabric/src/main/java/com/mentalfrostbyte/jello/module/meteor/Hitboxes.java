package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Hitboxes). Behaviour port pending. */
public class Hitboxes extends Module {
	private final NumberValue value1 = addValue(new NumberValue("expand", 0.5, 0, 100, 0.1));
	private final BooleanValue value2 = addValue(new BooleanValue("ignore-friends", true));
	private final BooleanValue value3 = addValue(new BooleanValue("only-on-weapon", false));
	private final BooleanValue value4 = addValue(new BooleanValue("sword", true));
	private final BooleanValue value5 = addValue(new BooleanValue("axe", true));
	private final BooleanValue value6 = addValue(new BooleanValue("pickaxe", true));
	private final BooleanValue value7 = addValue(new BooleanValue("shovel", true));
	private final BooleanValue value8 = addValue(new BooleanValue("hoe", true));
	private final BooleanValue value9 = addValue(new BooleanValue("mace", true));
	private final BooleanValue value10 = addValue(new BooleanValue("spear", true));
	private final BooleanValue value11 = addValue(new BooleanValue("trident", true));

	public Hitboxes() {
		super("Hitboxes", 0, Category.COMBAT);
	}
}
