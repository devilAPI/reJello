package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Crystal Aura). Behaviour port pending. */
public class CrystalAura extends Module {
	private final NumberValue value1 = addValue(new NumberValue("target-range", 10.0, 0.0, 100, 0.1));
	private final BooleanValue value2 = addValue(new BooleanValue("predict-movement", false));
	private final NumberValue value3 = addValue(new NumberValue("min-damage", 6.0, 0.0, 100, 0.1));
	private final NumberValue value4 = addValue(new NumberValue("max-damage", 6.0, 0.0, 36.0, 0.1));
	private final BooleanValue value5 = addValue(new BooleanValue("anti-suicide", true));
	private final BooleanValue value6 = addValue(new BooleanValue("ignore-nakeds", false));
	private final BooleanValue value7 = addValue(new BooleanValue("rotate", true));
	private final ModeValue value8 = addValue(new ModeValue("yaw-steps-mode", "Break", "Break", "B", "A"));
	private final NumberValue value9 = addValue(new NumberValue("yaw-steps", 180.0, 1.0, 180.0, 0.1));
	private final ModeValue value10 = addValue(new ModeValue("auto-switch", "Normal", "Normal", "N", "S", "N"));
	private final NumberValue value11 = addValue(new NumberValue("switch-delay", 0, 0, 100, 1));
	private final BooleanValue value12 = addValue(new BooleanValue("no-gap-switch", true));
	private final BooleanValue value13 = addValue(new BooleanValue("no-bow-switch", true));
	private final BooleanValue value14 = addValue(new BooleanValue("anti-weakness", true));
	private final BooleanValue value15 = addValue(new BooleanValue("place", true));
	private final NumberValue value16 = addValue(new NumberValue("place-delay", 0, 0, 100, 1));
	private final NumberValue value17 = addValue(new NumberValue("place-range", 4.5, 0.0, 100, 0.1));
	private final NumberValue value18 = addValue(new NumberValue("walls-range", 4.5, 0.0, 100, 0.1));
	private final BooleanValue value19 = addValue(new BooleanValue("1.12-placement", false));
	private final ModeValue value20 = addValue(new ModeValue("support", "Disabled", "Disabled", "D", "A", "F"));
	private final NumberValue value21 = addValue(new NumberValue("support-delay", 1, 0, 100, 1));
	private final BooleanValue value22 = addValue(new BooleanValue("face-place", true));
	private final NumberValue value23 = addValue(new NumberValue("face-place-health", 8.0, 1.0, 100, 0.1));
	private final NumberValue value24 = addValue(new NumberValue("face-place-durability", 2.0, 1.0, 100, 0.1));
	private final BooleanValue value25 = addValue(new BooleanValue("face-place-missing-armor", false));
	private final BooleanValue value26 = addValue(new BooleanValue("break", true));
	private final NumberValue value27 = addValue(new NumberValue("break-delay", 0, 0, 100, 1));
	private final BooleanValue value28 = addValue(new BooleanValue("smart-delay", false));
	private final NumberValue value29 = addValue(new NumberValue("break-range", 4.5, 0.0, 100, 0.1));
	private final NumberValue value30 = addValue(new NumberValue("walls-range 2", 4.5, 0.0, 100, 0.1));
	private final BooleanValue value31 = addValue(new BooleanValue("only-own", false));
	private final NumberValue value32 = addValue(new NumberValue("break-attempts", 2, 0, 100, 1));
	private final NumberValue value33 = addValue(new NumberValue("ticks-existed", 0, 0, 100, 1));
	private final NumberValue value34 = addValue(new NumberValue("attack-frequency", 25, 1, 30, 1));
	private final BooleanValue value35 = addValue(new BooleanValue("fast-break", true));
	private final ModeValue value36 = addValue(new ModeValue("pause-on-use", "Place", "Place", "B", "P", "B", "N"));
	private final ModeValue value37 = addValue(new ModeValue("pause-on-mine", "None", "None", "B", "P", "B", "N"));
	private final BooleanValue value38 = addValue(new BooleanValue("pause-on-lag", true));
	private final NumberValue value39 = addValue(new NumberValue("pause-health", 5.0, 0.0, 36.0, 0.1));
	private final ModeValue value40 = addValue(new ModeValue("swing-mode", "Both", "Both", "B", "P", "C", "N"));
	private final ModeValue value41 = addValue(new ModeValue("render-mode", "Normal", "Normal", "N", "S", "F", "G", "N"));
	private final BooleanValue value42 = addValue(new BooleanValue("render-place", true));
	private final NumberValue value43 = addValue(new NumberValue("place-time", 10, 0, 100, 1));
	private final BooleanValue value44 = addValue(new BooleanValue("render-break", false));
	private final NumberValue value45 = addValue(new NumberValue("break-time", 13, 0, 100, 1));
	private final NumberValue value46 = addValue(new NumberValue("smoothness", 10, 0, 100, 1));
	private final NumberValue value47 = addValue(new NumberValue("height", 0.7, 0.0, 100, 0.1));
	private final NumberValue value48 = addValue(new NumberValue("render-time", 10, 0, 100, 1));
	private final ModeValue value49 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));
	private final BooleanValue value50 = addValue(new BooleanValue("damage", true));
	private final NumberValue value51 = addValue(new NumberValue("damage-scale", 1.25, 1.0, 100, 0.1));

	public CrystalAura() {
		super("Crystal Aura", 0, Category.COMBAT);
	}
}
