package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.Vec3;

/** Server-side aura variant: attacks the nearest player without adding a setting set. */
public class HvHAura extends Module {
	private final TimerUtil timer = new TimerUtil();
	private LivingEntity target;

	public HvHAura() {
		super("HvHAura", 0, Category.COMBAT);
	}

	@Override
	public void onDisable() {
		target = null;
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.level == null) {
			return;
		}
		target = null;
		double best = 10;
		for (var entity : mc.level.entitiesForRendering()) {
			if (entity instanceof LivingEntity living && living != p && (living instanceof Player || living instanceof Monster)) {
				double distance = living.distanceTo(p);
				if (distance < best) {
					best = distance;
					target = living;
				}
			}
		}
		if (target == null) {
			return;
		}
		Vec3 eye = p.getEyePosition();
		Vec3 pos = target.position().add(0, target.getBbHeight() * 0.8, 0);
		double dx = pos.x - eye.x;
		double dy = pos.y - eye.y;
		double dz = pos.z - eye.z;
		p.setYRot((float) Math.toDegrees(Math.atan2(dz, dx)) - 90f);
		p.setXRot((float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))));
		if (timer.hasTimeElapsed(100, true)) {
			mc.gameMode.attack(p, target);
			p.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
		}
	}
}
