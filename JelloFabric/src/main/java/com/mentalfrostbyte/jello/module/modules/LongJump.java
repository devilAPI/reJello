package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMove;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.PlayerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.player.LocalPlayer;

/** The 1.8 "jump and glide": a long forward jump with a fixed air speed. */
public class LongJump extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	public static boolean enabled;
	public static float stage;
	private boolean wasOnGround;
	private boolean wasNotOnGround;

	private final ModeValue mode = addValue(new ModeValue("Mode", "Matrix", "Matrix", "Minemora", "MatrixBow"));
	private final NumberValue matrixBoost = addValue(new NumberValue("MatrixBoost", 1, 1, 3, 0.1));
	private final NumberValue matrixVerBoost = addValue(new NumberValue("MatrixVerBoost", 1, 0.1, 8, 0.1));

	public LongJump() {
		super("LongJump", InputConstants.KEY_B, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		wasOnGround = false;
		wasNotOnGround = false;
		stage = 0;
		enabled = true;
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
		if (p.onGround() && wasOnGround && wasNotOnGround) {
			setToggled(false);
			return;
		}
		if (p.onGround()) {
			wasOnGround = true;
		} else {
			wasNotOnGround = true;
		}
		if (PlayerUtil.isMoving() && p.onGround()) {
			double[] motion = PlayerUtil.motionFor(PlayerUtil.getBaseMoveSpeed() * 1.3);
			p.setDeltaMovement(motion[0], p.getDeltaMovement().y, motion[1]);
			p.jumpFromGround();
		}
		if (p.onGround()) {
			stage = 0;
		} else {
			if (p.getDeltaMovement().y > 0) {
				p.setDeltaMovement(p.getDeltaMovement().x, p.getDeltaMovement().y * 1.002 + 0.006, p.getDeltaMovement().z);
			} else if (p.fallDistance <= 2.6) {
				p.setDeltaMovement(p.getDeltaMovement().x, p.getDeltaMovement().y + 0.0366, p.getDeltaMovement().z);
			}
			stage += 0.7f + PlayerUtil.getSpeedAmplifier() * 0.45f;
		}
	}

	@EventTarget
	public void onMove(EventMove e) {
		if (!isToggled() || mc.player == null || mc.player.onGround()) {
			return;
		}
		float speed = 0.95f + PlayerUtil.getSpeedAmplifier() * 0.2f - stage / 25f;
		speed = Math.max(0.28f, speed);
		if (PlayerUtil.isMoving()) {
			double[] motion = PlayerUtil.motionFor(speed);
			e.x = motion[0];
			e.z = motion[1];
		}
	}
}
