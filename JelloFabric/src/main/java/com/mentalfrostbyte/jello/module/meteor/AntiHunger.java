package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

/** Meteor Client anti-hunger: spoofs sprint and on-ground flags to reduce hunger drain. */
public class AntiHunger extends Module {
	public AntiHunger() {
		super("Anti Hunger", 0, Category.PLAYER);
	}

	private final BooleanValue sprint = addValue(new BooleanValue("Sprint", true));
	private final BooleanValue onGround = addValue(new BooleanValue("On Ground", true));

	private boolean lastOnGround;
	private boolean ignorePacket;

	@Override
	public void onEnable() {
		if (mc.player != null) {
			lastOnGround = mc.player.onGround();
		}
		ignorePacket = false;
	}

	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (!isToggled() || mc.player == null) {
			return;
		}
		if (ignorePacket && event.getPacket() instanceof ServerboundMovePlayerPacket) {
			ignorePacket = false;
			return;
		}
		if (mc.player.isPassenger() || mc.player.isInWater() || mc.player.isUnderWater()) {
			return;
		}

		if (sprint.getValue() && event.getPacket() instanceof ServerboundPlayerCommandPacket packet
			&& packet.getAction() == ServerboundPlayerCommandPacket.Action.START_SPRINTING) {
			event.setCancelled(true);
			return;
		}

		if (onGround.getValue() && event.getPacket() instanceof ServerboundMovePlayerPacket packet
			&& mc.player.onGround() && mc.player.fallDistance <= 0.0F && !mc.gameMode.isDestroying()) {
			boolean collision = packet.horizontalCollision();
			ServerboundMovePlayerPacket replacement;
			if (packet.hasPosition() && packet.hasRotation()) {
				replacement = new ServerboundMovePlayerPacket.PosRot(packet.getX(0), packet.getY(0), packet.getZ(0),
					packet.getYRot(0), packet.getXRot(0), false, collision);
			} else if (packet.hasPosition()) {
				replacement = new ServerboundMovePlayerPacket.Pos(packet.getX(0), packet.getY(0), packet.getZ(0),
					false, collision);
			} else if (packet.hasRotation()) {
				replacement = new ServerboundMovePlayerPacket.Rot(packet.getYRot(0), packet.getXRot(0), false, collision);
			} else {
				replacement = new ServerboundMovePlayerPacket.StatusOnly(false, collision);
			}
			event.setPacket(replacement);
		}
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		// Never hide the landing itself, otherwise fall damage is cancelled too.
		if (mc.player.onGround() && !lastOnGround && onGround.getValue()) {
			ignorePacket = true;
		}
		lastOnGround = mc.player.onGround();
	}
}
