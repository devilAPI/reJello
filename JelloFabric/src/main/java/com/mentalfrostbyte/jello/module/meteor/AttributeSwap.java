package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Attribute Swap). Behaviour port pending. */
public class AttributeSwap extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Simple", "Simple", "C", "T", "B"));
	private final NumberValue value2 = addValue(new NumberValue("target-slot", 1, 1, 9, 1));
	private final BooleanValue value3 = addValue(new BooleanValue("swap-on-miss", false));
	private final BooleanValue value4 = addValue(new BooleanValue("swap-back", true));
	private final NumberValue value5 = addValue(new NumberValue("swap-back-delay", 2, 0, 20, 1));
	private final BooleanValue value6 = addValue(new BooleanValue("shield-breaker", true));
	private final BooleanValue value7 = addValue(new BooleanValue("durability-saver", true));
	private final BooleanValue value8 = addValue(new BooleanValue("sword-swapping", true));
	private final BooleanValue value9 = addValue(new BooleanValue("mace-swapping", true));
	private final BooleanValue value10 = addValue(new BooleanValue("spear-swapping", true));
	private final BooleanValue value11 = addValue(new BooleanValue("other-swapping", true));
	private final BooleanValue value12 = addValue(new BooleanValue("fire-aspect", true));
	private final BooleanValue value13 = addValue(new BooleanValue("looting", true));
	private final BooleanValue value14 = addValue(new BooleanValue("sharpness", true));
	private final BooleanValue value15 = addValue(new BooleanValue("smite", true));
	private final BooleanValue value16 = addValue(new BooleanValue("bane-of-arthropods", true));
	private final BooleanValue value17 = addValue(new BooleanValue("sweeping-edge", true));
	private final BooleanValue value18 = addValue(new BooleanValue("regular-mace", true));
	private final BooleanValue value19 = addValue(new BooleanValue("density", true));
	private final BooleanValue value20 = addValue(new BooleanValue("breach", true));
	private final BooleanValue value21 = addValue(new BooleanValue("wind-burst", true));
	private final BooleanValue value22 = addValue(new BooleanValue("impaling", true));
	private final BooleanValue value23 = addValue(new BooleanValue("lunge", true));
	private final BooleanValue value24 = addValue(new BooleanValue("hitbox", true));
	private final BooleanValue value25 = addValue(new BooleanValue("exclude-lunge-from-hitbox", true));
	private final BooleanValue value26 = addValue(new BooleanValue("only-on-weapon", false));
	private final BooleanValue value27 = addValue(new BooleanValue("sword", true));
	private final BooleanValue value28 = addValue(new BooleanValue("axe", true));
	private final BooleanValue value29 = addValue(new BooleanValue("pickaxe", true));
	private final BooleanValue value30 = addValue(new BooleanValue("shovel", true));
	private final BooleanValue value31 = addValue(new BooleanValue("hoe", true));
	private final BooleanValue value32 = addValue(new BooleanValue("mace", true));
	private final BooleanValue value33 = addValue(new BooleanValue("spear", true));
	private final BooleanValue value34 = addValue(new BooleanValue("trident", true));

	public AttributeSwap() {
		super("Attribute Swap", 0, Category.COMBAT);
	}
}
