package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.PlayerUtil;
import net.minecraft.client.player.LocalPlayer;

/** Circles around the KillAura target while jump is held. */
public class TargetStrafe extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "MultiplyRad", "MultiplyRad", "RadCheck"));
	private final NumberValue distance = addValue(new NumberValue("Distance", 1.0, 1.0, 6.0, 0.1));

	public TargetStrafe() {
		super("TargetStrafe", 0, Category.COMBAT);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || KillAura.getTarget() == null || !mc.options.keyJump.isDown()) {
			return;
		}
		var target = KillAura.getTarget();
		double dx = target.getX() - p.getX();
		double dz = target.getZ() - p.getZ();
		double angle = Math.atan2(dz, dx) + Math.PI / 2;
		double speed = PlayerUtil.getBaseMoveSpeed() * 1.1;
		double tangentX = Math.cos(angle) * speed;
		double tangentZ = Math.sin(angle) * speed;
		double towardsX = dx / Math.max(1, Math.abs(dx) + Math.abs(dz));
		double towardsZ = dz / Math.max(1, Math.abs(dx) + Math.abs(dz));
		if (p.distanceTo(target) > distance.getValue()) {
			p.setDeltaMovement(tangentX * 0.5 + towardsX * 0.2, p.getDeltaMovement().y, tangentZ * 0.5 + towardsZ * 0.2);
		} else {
			p.setDeltaMovement(tangentX, p.getDeltaMovement().y, tangentZ);
		}
	}
}
