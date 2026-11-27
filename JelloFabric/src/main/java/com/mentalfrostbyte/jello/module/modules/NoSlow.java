package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventSlowDown;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Removes the item-use slowdown (Delay/NCP/Matrix modes share the same 26.3 behaviour). */
public class NoSlow extends Module {
	public static boolean enabled;
	private final ModeValue mode = addValue(new ModeValue("Mode", "Delay", "Delay", "NCP", "Matrix"));

	public NoSlow() {
		super("NoSlow", 0, Category.MOVEMENT);
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
