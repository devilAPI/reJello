package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Ambience). Behaviour port pending. */
public class Ambience extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("end-sky", false));
	private final BooleanValue value2 = addValue(new BooleanValue("custom-sky-color", false));
	private final BooleanValue value3 = addValue(new BooleanValue("custom-cloud-color", false));
	private final BooleanValue value4 = addValue(new BooleanValue("custom-lightning-color", false));
	private final BooleanValue value5 = addValue(new BooleanValue("custom-grass-color", false));
	private final BooleanValue value6 = addValue(new BooleanValue("custom-foliage-color", false));
	private final BooleanValue value7 = addValue(new BooleanValue("custom-water-color", false));
	private final BooleanValue value8 = addValue(new BooleanValue("custom-lava-color", false));
	private final BooleanValue value9 = addValue(new BooleanValue("custom-fog-color", false));

	public Ambience() {
		super("Ambience", 0, Category.WORLD);
	}
}
