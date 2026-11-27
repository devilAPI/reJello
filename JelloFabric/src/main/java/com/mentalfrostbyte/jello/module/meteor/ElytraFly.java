package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Elytra Fly). Behaviour port pending. */
public class ElytraFly extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Vanilla", "Vanilla", "V", "P", "P", "B"));
	private final BooleanValue value2 = addValue(new BooleanValue("auto-take-off", false));
	private final NumberValue value3 = addValue(new NumberValue("fall-multiplier", 0.01, 0.0, 100, 0.1));
	private final NumberValue value4 = addValue(new NumberValue("horizontal-speed", 1.0, 0.0, 100, 0.1));
	private final NumberValue value5 = addValue(new NumberValue("vertical-speed", 1.0, 0.0, 100, 0.1));
	private final BooleanValue value6 = addValue(new BooleanValue("acceleration", false));
	private final NumberValue value7 = addValue(new NumberValue("acceleration-step", 1.0, 0.1, 5.0, 0.1));
	private final NumberValue value8 = addValue(new NumberValue("acceleration-start", 0.1, 0.1, 100, 0.1));
	private final BooleanValue value9 = addValue(new BooleanValue("stop-in-water", true));
	private final BooleanValue value10 = addValue(new BooleanValue("no-unloaded-chunks", true));
	private final BooleanValue value11 = addValue(new BooleanValue("auto-hover", false));
	private final BooleanValue value12 = addValue(new BooleanValue("no-crash", false));
	private final NumberValue value13 = addValue(new NumberValue("crash-look-ahead", 5, 1, 15, 1));
	private final BooleanValue value14 = addValue(new BooleanValue("insta-drop", false));
	private final NumberValue value15 = addValue(new NumberValue("pitch40-lower-bounds", 100, -128.0, 100, 0.1));
	private final NumberValue value16 = addValue(new NumberValue("pitch40-upper-bounds", 100, -128.0, 100, 0.1));
	private final NumberValue value17 = addValue(new NumberValue("pitch40-rotate-speed-up", 5.45, 1.0, 100, 0.1));
	private final NumberValue value18 = addValue(new NumberValue("pitch40-rotate-speed-down", 0.9, 0.5, 100, 0.1));
	private final BooleanValue value19 = addValue(new BooleanValue("auto-jump", true));
	private final ModeValue value20 = addValue(new ModeValue("yaw-lock", "Smart", "Smart"));
	private final NumberValue value21 = addValue(new NumberValue("yaw", 0.0, 0.0, 360.0, 0.1));
	private final BooleanValue value22 = addValue(new BooleanValue("pitch-lock", true));
	private final NumberValue value23 = addValue(new NumberValue("pitch", 85.0, 0.0, 90.0, 0.1));
	private final BooleanValue value24 = addValue(new BooleanValue("restart", true));
	private final NumberValue value25 = addValue(new NumberValue("restart-delay", 7, 0, 20, 1));
	private final BooleanValue value26 = addValue(new BooleanValue("sprint-constantly", true));
	private final BooleanValue value27 = addValue(new BooleanValue("manual-takeoff", false));
	private final BooleanValue value28 = addValue(new BooleanValue("elytra-replace", false));
	private final NumberValue value29 = addValue(new NumberValue("replace-durability", 2, 1, 500, 1));
	private final ModeValue value30 = addValue(new ModeValue("chest-swap", "Never", "Never", "A", "N", "W"));
	private final BooleanValue value31 = addValue(new BooleanValue("replenish-fireworks", false));
	private final NumberValue value32 = addValue(new NumberValue("replenish-slot", 9, 1, 9, 1));
	private final BooleanValue value33 = addValue(new BooleanValue("auto-pilot", false));
	private final BooleanValue value34 = addValue(new BooleanValue("use-fireworks", false));
	private final NumberValue value35 = addValue(new NumberValue("firework-delay", 8.0, 1.0, 100, 0.1));
	private final NumberValue value36 = addValue(new NumberValue("minimum-height", 100, -128.0, 100, 0.1));

	public ElytraFly() {
		super("Elytra Fly", 0, Category.MOVEMENT);
	}
}
