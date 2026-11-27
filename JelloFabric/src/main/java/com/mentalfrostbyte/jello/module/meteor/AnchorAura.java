package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Anchor Aura). Behaviour port pending. */
public class AnchorAura extends Module {
	private final NumberValue value1 = addValue(new NumberValue("target-range", 10.0, 0.0, 100, 0.1));
	private final ModeValue value2 = addValue(new ModeValue("target-priority", "LowestHealth", "LowestHealth", "L", "H", "L", "H", "C"));
	private final NumberValue value3 = addValue(new NumberValue("min-damage", 7.0, 0.0, 100, 0.1));
	private final NumberValue value4 = addValue(new NumberValue("max-self-damage", 7.0, 0.0, 100, 0.1));
	private final BooleanValue value5 = addValue(new BooleanValue("anti-suicide", true));
	private final BooleanValue value6 = addValue(new BooleanValue("swap-back", true));
	private final BooleanValue value7 = addValue(new BooleanValue("rotate", true));
	private final BooleanValue value8 = addValue(new BooleanValue("place", true));
	private final NumberValue value9 = addValue(new NumberValue("place-delay", 5, 0, 10, 1));
	private final NumberValue value10 = addValue(new NumberValue("place-range", 4.0, 0.0, 6.0, 0.1));
	private final NumberValue value11 = addValue(new NumberValue("walls-range", 4.0, 0.0, 6.0, 0.1));
	private final BooleanValue value12 = addValue(new BooleanValue("air-place", true));
	private final NumberValue value13 = addValue(new NumberValue("charge-delay", 1, 0, 10, 1));
	private final NumberValue value14 = addValue(new NumberValue("break-delay", 1, 0, 10, 1));
	private final NumberValue value15 = addValue(new NumberValue("break-range", 4.5, 0.0, 100, 0.1));
	private final NumberValue value16 = addValue(new NumberValue("walls-range 2", 4.5, 0.0, 100, 0.1));
	private final BooleanValue value17 = addValue(new BooleanValue("pause-on-use", true));
	private final BooleanValue value18 = addValue(new BooleanValue("pause-on-mine", true));
	private final BooleanValue value19 = addValue(new BooleanValue("pause-on-CA", true));
	private final BooleanValue value20 = addValue(new BooleanValue("swing", true));
	private final BooleanValue value21 = addValue(new BooleanValue("render", true));
	private final ModeValue value22 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public AnchorAura() {
		super("Anchor Aura", 0, Category.COMBAT);
	}
}
