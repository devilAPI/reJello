package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.util.PlayerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.player.LocalPlayer;

/**
 * Hover in place: the 1.8 original zeroed the motion and forced {@code onGround} every tick. 26.x applies
 * gravity inside {@code travel}, so the equivalent is to keep the player's own flight enabled while the module is on.
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
		if (mc.player != null) {
			mc.player.getAbilities().flying = false;
			mc.player.getAbilities().mayfly = false;
			mc.player.onUpdateAbilities();
		}
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
		p.getAbilities().mayfly = true;
		p.getAbilities().flying = true;
		p.onUpdateAbilities();
		// Data is only sent when a field actually changed; refresh every tick.
		p.getAbilities().flying = true;
		p.fallDistance = 0;
		counter++;
		if (counter > 1) {
			counter = 0;
		}
		if (PlayerUtil.isMoving() && !p.getAbilities().flying) {
			p.getAbilities().flying = true;
		}
	}
}
