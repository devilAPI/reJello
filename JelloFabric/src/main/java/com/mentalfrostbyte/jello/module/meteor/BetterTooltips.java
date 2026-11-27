package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Better Tooltips). Behaviour port pending. */
public class BetterTooltips extends Module {
	private final ModeValue value1 = addValue(new ModeValue("display-when", "Keybind", "Keybind", "K", "A"));
	private final BooleanValue value2 = addValue(new BooleanValue("open-contents", true));
	private final BooleanValue value3 = addValue(new BooleanValue("pause-in-creative", true));
	private final BooleanValue value4 = addValue(new BooleanValue("containers", true));
	private final BooleanValue value5 = addValue(new BooleanValue("compact-shulker-tooltip", true));
	private final BooleanValue value6 = addValue(new BooleanValue("echests", true));
	private final BooleanValue value7 = addValue(new BooleanValue("maps", true));
	private final NumberValue value8 = addValue(new NumberValue("map-scale", 1.0, 0.001, 100, 0.1));
	private final BooleanValue value9 = addValue(new BooleanValue("books", true));
	private final BooleanValue value10 = addValue(new BooleanValue("banners", true));
	private final BooleanValue value11 = addValue(new BooleanValue("entities-in-buckets", true));
	private final BooleanValue value12 = addValue(new BooleanValue("bundles", true));
	private final BooleanValue value13 = addValue(new BooleanValue("food-info", true));
	private final BooleanValue value14 = addValue(new BooleanValue("byte-size", true));
	private final ModeValue value15 = addValue(new ModeValue("byte-size-format", "Dynamic", "Dynamic", "B", "K", "M", "D"));
	private final BooleanValue value16 = addValue(new BooleanValue("status-effects", true));
	private final BooleanValue value17 = addValue(new BooleanValue("tooltip", false));
	private final BooleanValue value18 = addValue(new BooleanValue("tooltip-components", false));

	public BetterTooltips() {
		super("Better Tooltips", 0, Category.RENDER);
	}
}
