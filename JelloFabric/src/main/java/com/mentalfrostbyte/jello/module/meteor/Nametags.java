package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Nametags). Behaviour port pending. */
public class Nametags extends Module {
	private final NumberValue value1 = addValue(new NumberValue("scale", 1.1, 0.1, 100, 0.1));
	private final BooleanValue value2 = addValue(new BooleanValue("ignore-self", true));
	private final BooleanValue value3 = addValue(new BooleanValue("ignore-friends", false));
	private final BooleanValue value4 = addValue(new BooleanValue("ignore-bots", true));
	private final BooleanValue value5 = addValue(new BooleanValue("culling", false));
	private final NumberValue value6 = addValue(new NumberValue("culling-range", 20.0, 0.0, 100, 0.1));
	private final NumberValue value7 = addValue(new NumberValue("culling-count", 50, 1, 100, 1));
	private final BooleanValue value8 = addValue(new BooleanValue("health", true));
	private final BooleanValue value9 = addValue(new BooleanValue("use-display-name", false));
	private final BooleanValue value10 = addValue(new BooleanValue("gamemode", false));
	private final BooleanValue value11 = addValue(new BooleanValue("distance", false));
	private final BooleanValue value12 = addValue(new BooleanValue("ping", true));
	private final BooleanValue value13 = addValue(new BooleanValue("items", true));
	private final NumberValue value14 = addValue(new NumberValue("item-spacing", 2.0, 0.0, 10.0, 0.1));
	private final BooleanValue value15 = addValue(new BooleanValue("ignore-empty-slots", true));
	private final ModeValue value16 = addValue(new ModeValue("durability", "None", "None", "N", "T", "P"));
	private final BooleanValue value17 = addValue(new BooleanValue("display-enchants", false));
	private final ModeValue value18 = addValue(new ModeValue("enchantment-position", "Above", "Above", "A", "O"));
	private final NumberValue value19 = addValue(new NumberValue("enchant-name-length", 3, 1, 5, 1));
	private final NumberValue value20 = addValue(new NumberValue("enchant-text-scale", 1.0, 0.1, 2.0, 0.1));
	private final BooleanValue value21 = addValue(new BooleanValue("show-count", true));
	private final ModeValue value22 = addValue(new ModeValue("distance-color-mode", "Gradient", "Gradient", "G", "F"));

	public Nametags() {
		super("Nametags", 0, Category.RENDER);
	}
}
