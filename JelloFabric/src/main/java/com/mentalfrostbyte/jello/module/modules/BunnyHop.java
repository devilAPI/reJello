package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMove;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.util.PlayerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;

/** 1.8 BunnyHop: jump when the space bar is held, then keep a constant air speed. */
public class BunnyHop extends Module {
	public static boolean enabled;
	public static int stage;
	private double moveSpeed;
	private double lastDist;

	public BunnyHop() {
		super("Bunny Hop", InputConstants.KEY_V, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		enabled = true;
		moveSpeed = PlayerUtil.getBaseMoveSpeed();
		lastDist = 0;
		stage = 2;
	}

	@Override
	public void onDisable() {
		enabled = false;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		LocalPlayer p = mc.player;
		double dx = p.getX() - p.xo;
		double dz = p.getZ() - p.zo;
		lastDist = Math.sqrt(dx * dx + dz * dz);
	}

	@EventTarget
	public void onMove(EventMove e) {
		if (!isToggled() || mc.player == null) {
			return;
		}
		LocalPlayer p = mc.player;
		if (p.isInWater() || mc.options == null) {
			return;
		}
		if (!PlayerUtil.isMoving()) {
			moveSpeed = PlayerUtil.getBaseMoveSpeed();
		}
		if (stage == 1 && p.onGround() && PlayerUtil.isMoving()) {
			moveSpeed = 1.35 + PlayerUtil.getBaseMoveSpeed() - 0.01;
		}
		if (stage == 2 && p.onGround() && PlayerUtil.isNotCollidingBelow(0.01) && PlayerUtil.isMoving()) {
			if (mc.options.keyJump.isDown()) {
				double jump = 0.41999998688698;
				if (p.hasEffect(MobEffects.JUMP_BOOST)) {
					jump += (p.getEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1;
				}
				p.setDeltaMovement(p.getDeltaMovement().x, jump, p.getDeltaMovement().z);
				e.y = jump;
				moveSpeed *= 1.6;
			}
		} else if (stage == 3) {
			double reduction = 0.66 * (lastDist - PlayerUtil.getBaseMoveSpeed());
			moveSpeed = lastDist - reduction;
		} else {
			if (stage == 5 && p.getDeltaMovement().y > 0.1 && p.getDeltaMovement().y < 0.2) {
				p.setDeltaMovement(p.getDeltaMovement().x, 0, p.getDeltaMovement().z);
			}
			if (p.onGround() && stage > 0) {
				stage = PlayerUtil.isMoving() ? 1 : 0;
			}
			moveSpeed = lastDist - lastDist / 159.0;
		}
		moveSpeed = Math.max(moveSpeed, PlayerUtil.getBaseMoveSpeed());
		if (stage > 0 && PlayerUtil.isMoving()) {
			double[] motion = PlayerUtil.motionFor(moveSpeed);
			e.x = motion[0];
			e.z = motion[1];
		}
		if (PlayerUtil.isMoving()) {
			stage++;
		}
	}
}
