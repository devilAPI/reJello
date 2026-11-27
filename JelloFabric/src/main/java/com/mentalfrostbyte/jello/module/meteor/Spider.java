package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.client.player.LocalPlayer;

/** Meteor Client spider: climbs while pushing into a wall. */
public class Spider extends Module {
	public Spider() {
		super("Spider", 0, Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || !p.horizontalCollision) {
			return;
		}
		if (mc.options.keyJump.isDown()) {
			p.setDeltaMovement(p.getDeltaMovement().x, 0.2, p.getDeltaMovement().z);
		}
	}
}
