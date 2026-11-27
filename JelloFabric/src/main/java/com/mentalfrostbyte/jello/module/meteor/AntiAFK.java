package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.Random;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;

/** Meteor Client anti-afk: performs small actions so the server does not kick you. */
public class AntiAFK extends Module {
	public AntiAFK() {
		super("Anti AFK", 0, Category.PLAYER);
	}

	private final BooleanValue jump = addValue(new BooleanValue("Jump", true));
	private final BooleanValue swing = addValue(new BooleanValue("Swing", false));
	private final BooleanValue sneak = addValue(new BooleanValue("Sneak", false));
	private final NumberValue sneakTime = addValue(new NumberValue("Sneak Time", 5, 1, 100, 1));
	private final BooleanValue strafe = addValue(new BooleanValue("Strafe", false));
	private final BooleanValue spin = addValue(new BooleanValue("Spin", true));
	private final ModeValue spinMode = addValue(new ModeValue("Spin Mode", "Server", "Server", "Client"));
	private final NumberValue speed = addValue(new NumberValue("Speed", 7, 1, 30, 1));
	private final NumberValue pitch = addValue(new NumberValue("Pitch", 0, -90, 90, 1));

	private final Random random = new Random();
	private int sneakTimer;
	private int strafeTimer;
	private boolean direction;
	private float lastYaw;

	@Override
	public void onEnable() {
		if (mc.player != null) {
			lastYaw = mc.player.getYRot();
		}
		sneakTimer = 0;
		strafeTimer = 0;
	}

	@Override
	public void onDisable() {
		mc.options.keyLeft.setDown(false);
		mc.options.keyRight.setDown(false);
		mc.options.keyShift.setDown(false);
		mc.options.keyJump.setDown(false);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}

		if (jump.getValue()) {
			if (mc.options.keyJump.isDown()) {
				mc.options.keyJump.setDown(false);
			} else if (random.nextInt(99) == 0) {
				mc.options.keyJump.setDown(true);
			}
		}

		if (swing.getValue() && random.nextInt(99) == 0) {
			mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
		}

		if (sneak.getValue()) {
			if (sneakTimer++ >= sneakTime.getValue()) {
				mc.options.keyShift.setDown(false);
				if (random.nextInt(99) == 0) {
					sneakTimer = 0;
				}
			} else {
				mc.options.keyShift.setDown(true);
			}
		}

		if (strafe.getValue() && strafeTimer-- <= 0) {
			mc.options.keyLeft.setDown(!direction);
			mc.options.keyRight.setDown(direction);
			direction = !direction;
			strafeTimer = 20;
		}

		if (spin.getValue()) {
			lastYaw = net.minecraft.util.Mth.wrapDegrees(lastYaw + (float) speed.getValue());
			if (spinMode.is("Client")) {
				setClientYaw(lastYaw);
			}
		}
	}

	/** Server mode: rewrite the rotation in outgoing movement packets, leaving the local view alone. */
	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (!isToggled() || !spin.getValue() || !spinMode.is("Server")
			|| !(event.getPacket() instanceof ServerboundMovePlayerPacket packet)) {
			return;
		}
		float yaw = lastYaw;
		float pitch = (float) this.pitch.getValue();
		boolean ground = packet.isOnGround();
		boolean collision = packet.horizontalCollision();
		ServerboundMovePlayerPacket replacement;
		if (packet.hasPosition() && packet.hasRotation()) {
			replacement = new ServerboundMovePlayerPacket.PosRot(packet.getX(0), packet.getY(0), packet.getZ(0),
				yaw, pitch, ground, collision);
		} else if (packet.hasPosition()) {
			replacement = new ServerboundMovePlayerPacket.Pos(packet.getX(0), packet.getY(0), packet.getZ(0),
				ground, collision);
		} else if (packet.hasRotation()) {
			replacement = new ServerboundMovePlayerPacket.Rot(yaw, pitch, ground, collision);
		} else {
			replacement = new ServerboundMovePlayerPacket.StatusOnly(ground, collision);
		}
		event.setPacket(replacement);
	}

	private void setClientYaw(float yaw) {
		if (mc.player == null) {
			return;
		}
		mc.player.setYRot(yaw);
		mc.player.setYHeadRot(yaw);
		mc.player.setYBodyRot(yaw);
	}
}
