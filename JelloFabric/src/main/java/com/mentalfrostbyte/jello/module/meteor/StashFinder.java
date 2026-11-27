package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Stash Finder). Behaviour port pending. */
public class StashFinder extends Module {
	private final NumberValue value1 = addValue(new NumberValue("minimum-storage-count", 4, 1, 100, 1));
	private final NumberValue value2 = addValue(new NumberValue("minimum-distance", 0, 0, 100, 1));
	private final BooleanValue value3 = addValue(new BooleanValue("notifications", true));
	private final ModeValue value4 = addValue(new ModeValue("notification-mode", "Both", "Both", "C", "T", "B"));
	private final BooleanValue value5 = addValue(new BooleanValue("render-tracer", true));
	private final NumberValue value6 = addValue(new NumberValue("tracer-hide-at-distance", 16, 1, 100, 1));
	private final NumberValue value7 = addValue(new NumberValue("tracer-max-distance", 100, 10, 100, 1));
	private final BooleanValue value8 = addValue(new BooleanValue("render-chunk-column", false));

	public StashFinder() {
		super("Stash Finder", 0, Category.WORLD);
	}
}
