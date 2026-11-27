package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SwingAnimation;

/** Attacks every valid entity in the configured range (the 1.18 path packets are not reproduced). */
public class InfiniteAura extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 100, 1, 1000, 1));
	private final NumberValue aps = addValue(new NumberValue("APS", 1, 1, 10, 1));
	private final NumberValue maxTargets = addValue(new NumberValue("MaxTargets", 1, 1, 10, 1));
	private final NumberValue disBetweenPos = addValue(new NumberValue("DisBetweenPos", 3, 1, 5, 1));
	private final BooleanValue renderPath = addValue(new BooleanValue("RenderPath", true));
	private final TimerUtil timer = new TimerUtil();

	public InfiniteAura() {
		super("InfiniteAura", 0, Category.COMBAT);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.level == null) {
			return;
		}
		long interval = (long) (1000.0 / Math.max(1, aps.getValue()));
		if (!timer.hasTimeElapsed(interval, true)) {
			return;
		}
		int attacked = 0;
		for (var entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof LivingEntity living) || living == p || living.isDeadOrDying()) {
				continue;
			}
			if (!(living instanceof net.minecraft.world.entity.player.Player) && !(living instanceof net.minecraft.world.entity.monster.Monster)) {
				continue;
			}
			if (p.distanceTo(living) > range.getValue()) {
				continue;
			}
			mc.gameMode.attack(p, living);
			p.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
			if (++attacked >= (int) maxTargets.getValue()) {
				break;
			}
		}
	}
}
