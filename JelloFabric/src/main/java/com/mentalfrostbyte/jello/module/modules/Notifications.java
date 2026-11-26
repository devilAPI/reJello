package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Module;
import com.mojang.blaze3d.platform.InputConstants;

/** Master switch for the toast notifications (kept for parity with the original). */
public class Notifications extends Module {
	public static boolean enabled;

	public Notifications() {
		super("Notifications", 0, null);
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
