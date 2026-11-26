package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mojang.blaze3d.platform.InputConstants;

/** Kept as an empty module because the original HighJump had no behaviour either. */
public class HighJump extends Module {
	public static boolean enabled;

	public HighJump() {
		super("HighJump", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		enabled = true;
	}

	@Override
	public void onDisable() {
		enabled = false;
	}
}
