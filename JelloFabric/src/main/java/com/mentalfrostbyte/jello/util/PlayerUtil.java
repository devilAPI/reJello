package com.mentalfrostbyte.jello.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec2;

public final class PlayerUtil {
	private static final Minecraft mc = Minecraft.getInstance();

	private PlayerUtil() {
	}

	/** 1.8 getBaseMoveSpeed: 0.2873 plus 20% per Speed level. */
	public static double getBaseMoveSpeed() {
		double base = 0.2873;
		LocalPlayer p = mc.player;
		if (p != null && p.hasEffect(MobEffects.SPEED)) {
			base *= 1.0 + 0.2 * (p.getEffect(MobEffects.SPEED).getAmplifier() + 1);
		}
		return base;
	}

	public static int getSpeedAmplifier() {
		LocalPlayer p = mc.player;
		if (p != null && p.hasEffect(MobEffects.SPEED)) {
			return p.getEffect(MobEffects.SPEED).getAmplifier() + 1;
		}
		return 0;
	}

	/** True when there is a collision box just below the player (1.8 isNotCollidingBelow, which was misnamed). */
	public static boolean isNotCollidingBelow(double depth) {
		LocalPlayer p = mc.player;
		return p != null && mc.level != null && !mc.level.noCollision(p, p.getBoundingBox().move(0, -depth, 0));
	}

	public static float forward() {
		return mc.player == null ? 0 : mc.player.input.getMoveVector().y;
	}

	public static float strafe() {
		return mc.player == null ? 0 : mc.player.input.getMoveVector().x;
	}

	public static boolean isMoving() {
		if (mc.player == null) {
			return false;
		}
		Vec2 v = mc.player.input.getMoveVector();
		return v.x != 0 || v.y != 0;
	}

	/**
	 * Direction-aware horizontal motion of the given speed for the current inputs, as used by BunnyHop, BlockFly and
	 * AirWalk. Returns {x, z}. Note: 26.x strafe is positive to the LEFT, like 1.8's moveStrafe.
	 */
	public static double[] motionFor(double speed) {
		double forward = forward();
		double strafe = strafe();
		float yaw = mc.player.getYRot();
		if (forward == 0 && strafe == 0) {
			return new double[] {0, 0};
		}
		if (forward != 0) {
			if (strafe > 0) {
				yaw += forward > 0 ? -45 : 45;
			} else if (strafe < 0) {
				yaw += forward > 0 ? 45 : -45;
			}
			strafe = 0;
			forward = forward > 0 ? 1 : -1;
		}
		double cos = Math.cos(Math.toRadians(yaw + 90.0F));
		double sin = Math.sin(Math.toRadians(yaw + 90.0F));
		return new double[] {forward * speed * cos + strafe * speed * sin, forward * speed * sin - strafe * speed * cos};
	}
}
