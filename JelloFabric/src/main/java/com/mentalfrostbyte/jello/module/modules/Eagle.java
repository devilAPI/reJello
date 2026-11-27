package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.util.PlayerUtil;
import net.minecraft.client.player.LocalPlayer;

/** Sneaks automatically at the edge of a block. */
public class Eagle extends Module {
	public Eagle() {
		super("Eagle", 0, Category.MOVEMENT);
	}

	@Override
	public void onDisable() {
		mc.options.keyShift.setDown(false);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null) {
			return;
		}
		mc.options.keyShift.setDown(!PlayerUtil.isNotCollidingBelow(0.6));
	}
}
