package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

/** Tells the server the player is on the ground while falling. */
public class NoFall extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Normal", "Normal", "Matrix", "Matrix2", "Verus", "OldHypixel"));

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
