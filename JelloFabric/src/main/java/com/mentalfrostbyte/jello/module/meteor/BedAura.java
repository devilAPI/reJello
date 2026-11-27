package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Bed Aura). Behaviour port pending. */
public class BedAura extends Module {
	private final NumberValue value1 = addValue(new NumberValue("delay", 9, 0, 100, 1));
	private final BooleanValue value2 = addValue(new BooleanValue("strict-direction", false));
	private final NumberValue value3 = addValue(new NumberValue("target-range", 4.0, 0.0, 100, 0.1));
	private final ModeValue value4 = addValue(new ModeValue("target-priority", "LowestHealth", "LowestHealth", "L", "H", "L", "H", "C"));
	private final NumberValue value5 = addValue(new NumberValue("min-damage", 7.0, 0.0, 36.0, 0.1));
	private final NumberValue value6 = addValue(new NumberValue("max-self-damage", 7.0, 0.0, 36.0, 0.1));
	private final BooleanValue value7 = addValue(new BooleanValue("anti-suicide", true));
	private final BooleanValue value8 = addValue(new BooleanValue("auto-move", false));
	private final NumberValue value9 = addValue(new NumberValue("auto-move-slot", 9, 1, 9, 1));
	private final BooleanValue value10 = addValue(new BooleanValue("auto-switch", true));
	private final BooleanValue value11 = addValue(new BooleanValue("pause-on-eat", true));
	private final BooleanValue value12 = addValue(new BooleanValue("pause-on-drink", true));
	private final BooleanValue value13 = addValue(new BooleanValue("pause-on-mine", true));
	private final BooleanValue value14 = addValue(new BooleanValue("swing", true));
	private final BooleanValue value15 = addValue(new BooleanValue("render", true));
	private final ModeValue value16 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public BedAura() {
		super("Bed Aura", 0, Category.COMBAT);
	}
}
