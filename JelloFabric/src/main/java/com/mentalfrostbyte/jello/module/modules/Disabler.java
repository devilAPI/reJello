package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Anti-cheat disablers are server specific and are not reproduced; the setting set is kept. */
public class Disabler extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "BlockDropCombat", "BlockDropCombat", "BuzzTest", "BuzzTest2",
		"CubeCraft", "OldVerusMove", "Verus", "Verus Combat", "Kauri"));
	private final BooleanValue verusSilent = addValue(new BooleanValue("Verus Silent", true));
	private final BooleanValue debug = addValue(new BooleanValue("Debug", true));
	private final ModeValue verusDelay = addValue(new ModeValue("Verus Delay Mode", "Safety", "Safety", "Instant", "Extreme Fast",
		"Very Fast", "Super Fast", "Faster", "Fast", "Normal", "Slow", "Very Slow", "Super Slow", "Not Recommanded"));

	public Disabler() {
		super("Disabler", 0, Category.PLAYER);
	}
}
