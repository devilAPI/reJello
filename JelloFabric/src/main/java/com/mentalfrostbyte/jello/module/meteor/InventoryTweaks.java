package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Inventory Tweaks). Behaviour port pending. */
public class InventoryTweaks extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("mouse-drag-item-move", true));
	private final BooleanValue value2 = addValue(new BooleanValue("xcarry", true));
	private final BooleanValue value3 = addValue(new BooleanValue("uncap-bundle-scrolling", true));
	private final BooleanValue value4 = addValue(new BooleanValue("frame-input-handling", false));
	private final BooleanValue value5 = addValue(new BooleanValue("sorting-enabled", true));
	private final NumberValue value6 = addValue(new NumberValue("sorting-delay", 1, 0, 100, 1));
	private final BooleanValue value7 = addValue(new BooleanValue("disable-in-creative", true));
	private final BooleanValue value8 = addValue(new BooleanValue("item-frames", true));
	private final BooleanValue value9 = addValue(new BooleanValue("exclude-equipped", true));
	private final BooleanValue value10 = addValue(new BooleanValue("exclude-hotbar", false));
	private final BooleanValue value11 = addValue(new BooleanValue("only-full-stacks", false));
	private final BooleanValue value12 = addValue(new BooleanValue("inventory-buttons", true));
	private final BooleanValue value13 = addValue(new BooleanValue("steal-drop", false));
	private final BooleanValue value14 = addValue(new BooleanValue("drop-backwards", false));
	private final ModeValue value15 = addValue(new ModeValue("dump-filter", "None", "None", "W", "B"));
	private final ModeValue value16 = addValue(new ModeValue("steal-filter", "None", "None", "W", "B"));
	private final BooleanValue value17 = addValue(new BooleanValue("auto-steal", false));
	private final BooleanValue value18 = addValue(new BooleanValue("auto-dump", false));
	private final NumberValue value19 = addValue(new NumberValue("delay", 20, 0, 100, 1));
	private final NumberValue value20 = addValue(new NumberValue("initial-delay", 50, 0, 100, 1));
	private final NumberValue value21 = addValue(new NumberValue("random", 50, 0, 100, 1));

	public InventoryTweaks() {
		super("Inventory Tweaks", 0, Category.MISC);
	}
}
