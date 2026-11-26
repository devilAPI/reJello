package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mojang.blaze3d.platform.InputConstants;

/** The active-module list on the right edge; drawn by {@link com.mentalfrostbyte.jello.hud.JelloHud}. */
public class ActiveMods extends Module {
	public static boolean enabled;

	public ActiveMods() {
		super("ActiveMods", 0, Category.RENDER);
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
