package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.util.PlayerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.player.LocalPlayer;

/**
 * Hover in place. The 1.8 original zeroed vertical motion and forced {@code onGround} every tick; 26.x applies
 * gravity inside {@code travel}, so gravity is cancelled for the local player through a mixin (see
 * {@link com.mentalfrostbyte.jello.mixin.LivingEntityMixin}) while this module is on.
 */
public class AirWalk extends Module {
	public static boolean isEnabled;
	private int counter;

	public AirWalk() {
		super("AirWalk", InputConstants.KEY_G, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		isEnabled = true;
		counter = 0;
	}

	@Override
	public void onDisable() {
		isEnabled = false;
		counter = 0;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		LocalPlayer p = mc.player;
		if (p.hurtTime == 19) {
			p.setDeltaMovement(0, 0, 0);
		}
		p.fallDistance = 0;
		p.setOnGround(true);
		if (!mc.options.keyJump.isDown()) {
			p.setDeltaMovement(p.getDeltaMovement().x, 0, p.getDeltaMovement().z);
		}
		counter = (counter + 1) % 2;
	}
}
