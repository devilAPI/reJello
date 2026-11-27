package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Wurst Follow: walks after the nearest player or animal. */
public class Follow extends Module {
	private final NumberValue distance = addValue(new NumberValue("Distance", 3, 1, 12, 0.5));
	private final ModeValue targetType = addValue(new ModeValue("Target", "Players", "Players", "Animals", "Both"));

	private Entity target;

	public Follow() {
		super("Follow", 0, Category.MOVEMENT);
	}

	@Override
	public void onDisable() {
		mc.options.keyUp.setDown(false);
		target = null;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (target == null || !target.isAlive() || mc.level.getEntity(target.getId()) == null) {
			target = findTarget();
		}
		if (target == null) {
			mc.options.keyUp.setDown(false);
			return;
		}
		lookAt(target.getBoundingBox().getCenter());
		mc.options.keyUp.setDown(mc.player.distanceTo(target) > distance.getValue());
		if (mc.player.horizontalCollision && mc.player.onGround()) {
			mc.player.jumpFromGround();
		}
		if (mc.player.isInWater() && mc.player.getY() < target.getY()) {
			mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(0, 0.04, 0));
		}
	}

	private Entity findTarget() {
		Entity best = null;
		double bestDistance = 64 * 64;
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof LivingEntity living) || entity == mc.player || living.isDeadOrDying()) {
				continue;
			}
			boolean isPlayer = living instanceof Player;
			boolean isAnimal = living instanceof Animal;
			if (isPlayer && targetType.is("Animals")) {
				continue;
			}
			if (isAnimal && targetType.is("Players")) {
				continue;
			}
			if (!isPlayer && !isAnimal) {
				continue;
			}
			double distanceSq = mc.player.distanceToSqr(living);
			if (distanceSq < bestDistance) {
				bestDistance = distanceSq;
				best = living;
			}
		}
		return best;
	}

	private void lookAt(Vec3 target) {
		Vec3 eye = mc.player.getEyePosition();
		double dx = target.x - eye.x;
		double dy = target.y - eye.y;
		double dz = target.z - eye.z;
		mc.player.setYRot((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
		mc.player.setXRot((float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))));
	}
}
