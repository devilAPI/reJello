package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.client.player.LocalPlayer;

/** Meteor Client air-jump: one extra jump while airborne. */
public class AirJump extends Module {
	private boolean used;

	public AirJump() {
		super("Air Jump", 0, Category.MOVEMENT);
	}

	@Override
	public void onDisable() {
		used = false;
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null) {
			return;
		}
		if (p.onGround()) {
			used = false;
			return;
		}
		if (!used && mc.options.keyJump.isDown()) {
			p.setDeltaMovement(p.getDeltaMovement().x, 0.42, p.getDeltaMovement().z);
			used = true;
		}
	}
}
