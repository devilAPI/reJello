package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventSlowDown;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mojang.blaze3d.platform.InputConstants;

/** Removes the movement penalty while eating, blocking or drawing a bow. */
public class NoSlow extends Module {
	public static boolean enabled;

	public NoSlow() {
		super("NoSlow", 0, Category.MOVEMENT);
		addValue(new com.mentalfrostbyte.jello.setting.BooleanValue("Run", false));
		addValue(new com.mentalfrostbyte.jello.setting.BooleanValue("Step", false));
		addValue(new com.mentalfrostbyte.jello.setting.BooleanValue("Brakes", false));
	}

	@Override
	public void onEnable() {
		enabled = true;
	}

	@Override
	public void onDisable() {
		enabled = false;
	}

	@EventTarget
	public void onSlowDown(EventSlowDown event) {
		event.setCancelled(true);
	}
}
