package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventAttack;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

/** Critical-hit packet modes. */
public class Criticals extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Packet", "Packet", "Jump", "PacketLegit", "MatrixSemi"));

	public Criticals() {
		super("Criticals", 0, Category.COMBAT);
	}

	@EventTarget
	public void onAttack(EventAttack event) {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.getConnection() == null) {
			return;
		}
		switch (mode.getMode()) {
			case "Jump" -> {
				if (p.onGround()) {
					p.jumpFromGround();
				}
			}
			case "PacketLegit" -> {
				if (!p.onGround()) {
					mc.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(false, p.horizontalCollision));
				}
			}
			default -> {
				if (p.onGround()) {
					double y = p.getY();
					mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(p.getX(), y + 0.0626, p.getZ(), false, false));
					mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(p.getX(), y, p.getZ(), false, false));
				}
			}
		}
	}
}
