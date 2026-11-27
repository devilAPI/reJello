package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.SpectralArrow;
import net.minecraft.world.phys.Vec3;

/** Meteor Client arrow-dodge: simulates incoming projectiles and sidesteps them. */
public class ArrowDodge extends Module {
	private final ModeValue moveType = addValue(new ModeValue("Move Type", "Velocity", "Velocity", "Packet"));
	private final NumberValue moveSpeed = addValue(new NumberValue("Move Speed", 1.0, 0.01, 5.0, 0.05));
	private final NumberValue distanceCheck = addValue(new NumberValue("Distance Check", 1.0, 0.01, 5.0, 0.05));
	private final BooleanValue groundCheck = addValue(new BooleanValue("Ground Check", true));
	private final BooleanValue allProjectiles = addValue(new BooleanValue("All Projectiles", false));
	private final BooleanValue ignoreOwn = addValue(new BooleanValue("Ignore Own", true));
	private final NumberValue simulationSteps = addValue(new NumberValue("Simulation Steps", 100, 0, 1000, 10));

	private static final Vec3[] DIRECTIONS = {
		new Vec3(1, 0, 1), new Vec3(0, 0, 1), new Vec3(-1, 0, 1),
		new Vec3(1, 0, 0), new Vec3(-1, 0, 0),
		new Vec3(1, 0, -1), new Vec3(0, 0, -1), new Vec3(-1, 0, -1)
	};

	public ArrowDodge() {
		super("Arrow Dodge", 0, Category.COMBAT);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}

		List<Vec3> points = simulateProjectiles();
		if (points.isEmpty() || isValid(Vec3.ZERO, false, points)) {
			return;
		}

		List<Vec3> directions = new ArrayList<>(List.of(DIRECTIONS));
		double speed = moveSpeed.getValue();
		for (int attempt = 0; attempt < 20; attempt++) {
			Collections.shuffle(directions);
			for (Vec3 direction : directions) {
				Vec3 velocity = direction.scale(speed);
				if (isValid(velocity, true, points)) {
					move(velocity);
					return;
				}
			}
			speed += moveSpeed.getValue();
		}
	}

	private List<Vec3> simulateProjectiles() {
		List<Vec3> points = new ArrayList<>();
		if (mc.level == null || mc.player == null) {
			return points;
		}
		int steps = (int) simulationSteps.getValue();
		if (steps <= 0) {
			steps = 300;
		}
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof Projectile projectile)) {
				continue;
			}
			if (!allProjectiles.getValue() && !(projectile instanceof Arrow) && !(projectile instanceof SpectralArrow)) {
				continue;
			}
			if (ignoreOwn.getValue()) {
				Entity owner = projectile.getOwner();
				if (owner != null && owner.getUUID().equals(mc.player.getUUID())) {
					continue;
				}
			}

			Vec3 pos = projectile.position();
			Vec3 velocity = projectile.getDeltaMovement();
			for (int i = 0; i < steps; i++) {
				pos = pos.add(velocity);
				velocity = new Vec3(velocity.x * 0.99, velocity.y * 0.99 - 0.05, velocity.z * 0.99);
				points.add(pos);
				if (velocity.lengthSqr() < 1.0e-4) {
					break;
				}
			}
		}
		return points;
	}

	private void move(Vec3 velocity) {
		if (mc.player == null) {
			return;
		}
		if (moveType.is("Packet")) {
			Vec3 pos = mc.player.position().add(velocity);
			if (mc.getConnection() != null) {
				mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(pos.x, pos.y, pos.z,
					mc.player.onGround(), mc.player.horizontalCollision));
			}
		} else {
			mc.player.setDeltaMovement(velocity.x, 0.0, velocity.z);
		}
	}

	private boolean isValid(Vec3 velocity, boolean checkGround, List<Vec3> points) {
		if (mc.player == null || mc.level == null) {
			return false;
		}
		Vec3 playerPos = mc.player.position().add(velocity);
		Vec3 headPos = playerPos.add(0, 1, 0);
		for (Vec3 point : points) {
			if (point.closerThan(playerPos, distanceCheck.getValue())
				|| point.closerThan(headPos, distanceCheck.getValue())) {
				return false;
			}
		}

		if (checkGround) {
			BlockPos pos = BlockPos.containing(playerPos);
			if (!mc.level.getBlockState(pos).getCollisionShape(mc.level, pos).isEmpty()) {
				return false;
			}
			if (!mc.level.getBlockState(pos.above()).getCollisionShape(mc.level, pos.above()).isEmpty()) {
				return false;
			}
			if (groundCheck.getValue()
				&& mc.level.getBlockState(pos.below()).getCollisionShape(mc.level, pos.below()).isEmpty()) {
				return false;
			}
		}
		return true;
	}
}
