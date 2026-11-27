package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.client.player.LocalPlayer;

/** Ghosts through blocks (Matrix mode) instead of the 1.18 packet exploit. */
public class Phase extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Matrix", "Matrix", "VClip"));

	public Phase() {
		super("Phase", 0, Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null) {
			return;
		}
		p.noPhysics = true;
	}

	@Override
	public void onDisable() {
		if (mc.player != null) {
			mc.player.noPhysics = false;
		}
	}
}
