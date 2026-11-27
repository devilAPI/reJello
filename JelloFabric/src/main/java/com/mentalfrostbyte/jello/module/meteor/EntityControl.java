package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.PositionAndRotation;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.phys.Vec3;

/** Meteor Client entity-control: speed, fly and yaw lock for ridden entities. */
public class EntityControl extends Module {
	private final BooleanValue spoofSaddle = addValue(new BooleanValue("Spoof Saddle", false));
	private final BooleanValue maxJump = addValue(new BooleanValue("Max Jump", true));
	private final BooleanValue lockYaw = addValue(new BooleanValue("Lock Yaw", true));
	private final BooleanValue cancelServerPackets = addValue(new BooleanValue("Cancel Server Packets", false));

	private final BooleanValue speed = addValue(new BooleanValue("Speed", false));
	private final NumberValue horizontalSpeed = addValue(new NumberValue("Horizontal Speed", 10, 0, 50, 0.1));
	private final BooleanValue onlyOnGround = addValue(new BooleanValue("Only On Ground", false));
	private final BooleanValue inWater = addValue(new BooleanValue("In Water", true));

	private final BooleanValue fly = addValue(new BooleanValue("Fly", false));
	private final NumberValue verticalSpeed = addValue(new NumberValue("Vertical Speed", 6, 0, 20, 0.1));
	private final NumberValue fallSpeed = addValue(new NumberValue("Fall Speed", 0, 0, 10, 0.1));
	private final BooleanValue antiFlyKick = addValue(new BooleanValue("Anti Fly Kick", true));
	private final NumberValue delay = addValue(new NumberValue("Delay", 40, 1, 100, 1));

	private int delayLeft;
	private double lastPacketY = Double.MAX_VALUE;
	private boolean sentPacket;

	public EntityControl() {
		super("Entity Control", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		delayLeft = (int) delay.getValue();
		sentPacket = false;
		lastPacketY = Double.MAX_VALUE;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		Entity vehicle = mc.player.getVehicle();
		if (vehicle == null || vehicle.getControllingPassenger() != mc.player) {
			return;
		}

		if (sentPacket) {
			// Snap back to the real height on the next tick, like Meteor's packet rewrite.
			ServerboundMoveVehiclePacket packet = ServerboundMoveVehiclePacket.fromEntity(vehicle);
			PositionAndRotation rot = packet.movingTo();
			Vec3 pos = rot.position();
			Vec3 restored = new Vec3(pos.x, lastPacketY, pos.z);
			send(new ServerboundMoveVehiclePacket(PositionAndRotation.of(restored, rot.yRot(), rot.xRot()), packet.onGround()));
			sentPacket = false;
		}

		delayLeft--;

		double velX = vehicle.getDeltaMovement().x;
		double velY = vehicle.getDeltaMovement().y;
		double velZ = vehicle.getDeltaMovement().z;

		if (speed.getValue() && (!onlyOnGround.getValue() || vehicle.onGround() || vehicle.isFlyingVehicle())
			&& (inWater.getValue() || !vehicle.isInWater())) {
			double bps = horizontalSpeed.getValue();
			Vec3 forward = Vec3.directionFromRotation(0, mc.player.getYRot());
			Vec3 right = Vec3.directionFromRotation(0, mc.player.getYRot() + 90);
			double accX = 0;
			double accZ = 0;
			boolean a = false;
			boolean b = false;
			if (mc.player.input.keyPresses.forward()) {
				accX += forward.x / 20 * bps;
				accZ += forward.z / 20 * bps;
				a = true;
			}
			if (mc.player.input.keyPresses.backward()) {
				accX -= forward.x / 20 * bps;
				accZ -= forward.z / 20 * bps;
				a = true;
			}
			if (mc.player.input.keyPresses.right()) {
				accX += right.x / 20 * bps;
				accZ += right.z / 20 * bps;
				b = true;
			}
			if (mc.player.input.keyPresses.left()) {
				accX -= right.x / 20 * bps;
				accZ -= right.z / 20 * bps;
				b = true;
			}
			if (a && b) {
				accX *= 0.7071067811865476;
				accZ *= 0.7071067811865476;
			}
			velX = accX;
			velZ = accZ;
		}

		if (fly.getValue()) {
			velY = 0;
			if (mc.options.keyJump.isDown()) {
				velY += verticalSpeed.getValue() / 20;
			}
			if (mc.options.keySprint.isDown()) {
				velY -= verticalSpeed.getValue() / 20;
			} else {
				velY -= fallSpeed.getValue() / 20;
			}
		}

		if (lockYaw.getValue()) {
			vehicle.setYRot(mc.player.getYRot());
		}
		vehicle.setDeltaMovement(velX, velY, velZ);

		double currentY = vehicle.getY();
		if (fly.getValue() && antiFlyKick.getValue() && delayLeft <= 0 && !sentPacket
			&& shouldFlyDown(currentY) && isOnAir(vehicle) && !vehicle.isFlyingVehicle()) {
			ServerboundMoveVehiclePacket packet = ServerboundMoveVehiclePacket.fromEntity(vehicle);
			PositionAndRotation rot = packet.movingTo();
			Vec3 pos = rot.position();
			Vec3 lowered = new Vec3(pos.x, currentY - 0.03130D, pos.z);
			send(new ServerboundMoveVehiclePacket(PositionAndRotation.of(lowered, rot.yRot(), rot.xRot()), packet.onGround()));
			sentPacket = true;
			delayLeft = (int) delay.getValue();
		}
		lastPacketY = currentY;
	}

	private boolean shouldFlyDown(double currentY) {
		if (currentY >= lastPacketY) {
			return true;
		}
		return lastPacketY - currentY < 0.03130D;
	}

	@EventTarget
	public void onReceivePacket(EventReceivePacket event) {
		if (isToggled() && cancelServerPackets.getValue() && event.getPacket() instanceof ClientboundMoveVehiclePacket) {
			event.setCancelled(true);
		}
	}

	private void send(ServerboundMoveVehiclePacket packet) {
		if (mc.getConnection() != null) {
			mc.getConnection().send(packet);
		}
	}

	private boolean isOnAir(Entity entity) {
		return mc.level.getBlockStates(entity.getBoundingBox().inflate(0.0625).expandTowards(0.0, -0.55, 0.0))
			.allMatch(state -> state.isAir());
	}

	public boolean spoofSaddle() {
		return isToggled() && spoofSaddle.getValue();
	}

	public boolean maxJump() {
		return isToggled() && maxJump.getValue();
	}

	public boolean cancelJump() {
		Entity vehicle = mc.player == null ? null : mc.player.getVehicle();
		if (!(vehicle instanceof PlayerRideableJumping)) {
			return false;
		}
		return isToggled() && fly.getValue();
	}
}
