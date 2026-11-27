package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.client.gui.screens.DeathScreen;

/** Meteor Client auto-respawn: leaves the death screen immediately. */
public class AutoRespawn extends Module {
	public AutoRespawn() {
		super("Auto Respawn", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || !(mc.gui.screen() instanceof DeathScreen)) {
			return;
		}
		mc.player.respawn();
		mc.gui.setScreen(null);
	}
}
