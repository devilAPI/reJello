package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

/** Tells the server the player is on the ground while falling. */
public class NoFall extends Module {
	public NoFall() {
		super("NoFall", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.getConnection() == null) {
			return;
		}
		if (mc.player.fallDistance > 2 && mc.player.fallDistance < 20 && !AirWalk.isEnabled && !Jello.isEnabled("Rush")) {
			mc.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(true, mc.player.horizontalCollision));
		}
	}
}
