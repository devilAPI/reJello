package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Movement sanity checks; the 26.3 client already rejects most invalid server moves. */
public class AntiCheat extends Module {
	private final BooleanValue moveCheck = addValue(new BooleanValue("Move Check", true));

	public AntiCheat() {
		super("AntiCheat", 0, Category.PLAYER);
	}
}
