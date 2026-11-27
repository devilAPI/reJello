package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.Vec3;

/** Wurst FightBot: walks to nearby enemies, keeps within range and attacks them. */
public class FightBot extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 4.25, 1, 6, 0.05));
	private final NumberValue distance = addValue(new NumberValue("Distance", 3, 1, 6, 0.05));
	private final NumberValue aps = addValue(new NumberValue("APS", 8, 1, 20, 1));
	private final BooleanValue players = addValue(new BooleanValue("Players", true));
	private final BooleanValue mobs = addValue(new BooleanValue("Mobs", true));

	private final TimerUtil timer = new TimerUtil();
	private LivingEntity target;

	public FightBot() {
		super("Fight Bot", 0, Category.COMBAT);
	}

	@Override
	public void onDisable() {
		mc.options.keyUp.setDown(false);
		target = null;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		target = findTarget();
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
		if (mc.player.distanceTo(target) > range.getValue()) {
			return;
		}
		long interval = (long) (1000.0 / Math.max(1, aps.getValue()));
		if (!timer.hasTimeElapsed(interval, true)) {
			return;
		}
		mc.gameMode.attack(mc.player, target);
		mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
	}

	private LivingEntity findTarget() {
		LivingEntity best = null;
		double bestDistance = 24 * 24;
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof LivingEntity living) || entity == mc.player || living.isDeadOrDying()
				|| living.isInvisible()) {
				continue;
			}
			if (living instanceof Player) {
				if (!players.getValue()) {
					continue;
				}
			} else if (living instanceof Monster) {
				if (!mobs.getValue()) {
					continue;
				}
			} else {
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
