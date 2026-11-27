package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.Vec3;

/** Wurst MultiAura: attacks every valid entity in range at once. */
public class MultiAura extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 5, 1, 6, 0.05));
	private final NumberValue aps = addValue(new NumberValue("APS", 10, 1, 20, 1));
	private final BooleanValue players = addValue(new BooleanValue("Players", true));
	private final BooleanValue mobs = addValue(new BooleanValue("Mobs", true));

	private final TimerUtil timer = new TimerUtil();

	public MultiAura() {
		super("Multi Aura", 0, Category.COMBAT);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		long interval = (long) (1000.0 / Math.max(1, aps.getValue()));
		if (!timer.hasTimeElapsed(interval, true)) {
			return;
		}
		List<LivingEntity> targets = new ArrayList<>();
		double rangeSq = range.getValue() * range.getValue();
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof LivingEntity living) || entity == mc.player || living.isDeadOrDying()
				|| living.isInvisible() || mc.player.distanceToSqr(living) > rangeSq) {
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
			targets.add(living);
		}
		if (targets.isEmpty()) {
			return;
		}
		for (LivingEntity target : targets) {
			lookAt(target.getBoundingBox().getCenter());
			mc.gameMode.attack(mc.player, target);
		}
		mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
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
