package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Highway Builder). Behaviour port pending. */
public class HighwayBuilder extends Module {
	private final NumberValue value1 = addValue(new NumberValue("width", 4, 1, 5, 1));
	private final NumberValue value2 = addValue(new NumberValue("height", 3, 2, 5, 1));
	private final ModeValue value3 = addValue(new ModeValue("floor", "Replace", "Replace", "R", "P"));
	private final BooleanValue value4 = addValue(new BooleanValue("railings", true));
	private final BooleanValue value5 = addValue(new BooleanValue("corner-support-block", true));
	private final BooleanValue value6 = addValue(new BooleanValue("mine-above-railings", true));
	private final ModeValue value7 = addValue(new ModeValue("rotation", "Both", "Both", "N", "M", "P", "B"));
	private final BooleanValue value8 = addValue(new BooleanValue("disconnect-on-toggle", false));
	private final BooleanValue value9 = addValue(new BooleanValue("pause-on-lag", true));
	private final BooleanValue value10 = addValue(new BooleanValue("destroy-crystal-traps", true));
	private final BooleanValue value11 = addValue(new BooleanValue("double-mine", true));
	private final BooleanValue value12 = addValue(new BooleanValue("fast-break", true));
	private final BooleanValue value13 = addValue(new BooleanValue("dont-break-tools", false));
	private final NumberValue value14 = addValue(new NumberValue("durability-percentage", 2, 1, 100, 1));
	private final NumberValue value15 = addValue(new NumberValue("save-pickaxes", 1, 0, 36, 1));
	private final NumberValue value16 = addValue(new NumberValue("break-delay", 0, 0, 100, 1));
	private final NumberValue value17 = addValue(new NumberValue("blocks-per-tick", 1, 1, 100, 1));
	private final NumberValue value18 = addValue(new NumberValue("place-range", 4.5, 0, 100, 0.1));
	private final NumberValue value19 = addValue(new NumberValue("place-delay", 0, 0, 100, 1));
	private final NumberValue value20 = addValue(new NumberValue("placements-per-tick", 1, 1, 100, 1));
	private final NumberValue value21 = addValue(new NumberValue("inventory-delay", 3, 0, 100, 1));
	private final BooleanValue value22 = addValue(new BooleanValue("eject-useless-shulkers", true));
	private final BooleanValue value23 = addValue(new BooleanValue("search-ender-chest", false));
	private final BooleanValue value24 = addValue(new BooleanValue("search-shulkers", true));
	private final NumberValue value25 = addValue(new NumberValue("minimum-empty-slots", 3, 0, 9, 1));
	private final BooleanValue value26 = addValue(new BooleanValue("mine-ender-chests", true));
	private final ModeValue value27 = addValue(new ModeValue("echest-blockade-type", "Full", "Full", "F", "P", "S"));
	private final NumberValue value28 = addValue(new NumberValue("save-ender-chests", 2, 0, 64, 1));
	private final BooleanValue value29 = addValue(new BooleanValue("instantly-rebreak-echests", false));
	private final NumberValue value30 = addValue(new NumberValue("rebreak-delay", 0, 0, 100, 1));
	private final BooleanValue value31 = addValue(new BooleanValue("render-blocks-to-mine", true));
	private final ModeValue value32 = addValue(new ModeValue("blocks-to-mine-shape-mode", "Both", "Both", "L", "S", "B"));
	private final BooleanValue value33 = addValue(new BooleanValue("render-blocks-to-place", true));
	private final ModeValue value34 = addValue(new ModeValue("blocks-to-place-shape-mode", "Both", "Both", "L", "S", "B"));

	public HighwayBuilder() {
		super("Highway Builder", 0, Category.WORLD);
	}
}
