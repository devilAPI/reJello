package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.Vec3;

/** Rotates toward the nearest entity within the configured angles. */
public class AimAssist extends Module {
	private final NumberValue horizontal = addValue(new NumberValue("Horizontal", 4.2, 0.0, 95.0, 1.0));
	private final NumberValue vertical = addValue(new NumberValue("Vertical", 2.4, 0.0, 95.0, 1.0));
	private final NumberValue range = addValue(new NumberValue("Range", 3.2, 1.0, 6.0, 0.5));
	private final NumberValue angleMin = addValue(new NumberValue("AngleMin", 0.0, 0.0, 2.0, 1.0));
	private final NumberValue angleMax = addValue(new NumberValue("AngleMax", 100.0, 20.0, 360.0, 1.0));
	private final BooleanValue clickOnly = addValue(new BooleanValue("Click Only", true));
	private final BooleanValue strafe = addValue(new BooleanValue("Strafe", false));
	private final BooleanValue team = addValue(new BooleanValue("Team Check", true));
	private final BooleanValue itemCheck = addValue(new BooleanValue("Item Check", true));
	private final BooleanValue legitAura = addValue(new BooleanValue("Legit Aura", true));
	private final BooleanValue noSwing = addValue(new BooleanValue("Legit Aura NoSwing", true));
	private final BooleanValue block = addValue(new BooleanValue("Legit Aura Block", true));
	private final TimerUtil timer = new TimerUtil();

	public AimAssist() {
		super("AimAssist", 0, Category.COMBAT);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.level == null) {
			return;
		}
		if (clickOnly.getValue() && !mc.options.keyAttack.isDown()) {
			return;
		}
		if (itemCheck.getValue() && p.getMainHandItem().isEmpty()) {
			return;
		}
		LivingEntity best = null;
		double bestAngle = Double.MAX_VALUE;
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof LivingEntity living) || living == p || living.isDeadOrDying() || living.isInvisible()) {
				continue;
			}
			if (living.distanceTo(p) > range.getValue()) {
				continue;
			}
			if (team.getValue() && p.getTeam() == living.getTeam()) {
				continue;
			}
			if (!(living instanceof Player) && !(living instanceof net.minecraft.world.entity.monster.Monster)) {
				continue;
			}
			float[] rotations = rotationsTo(p, living);
			double diff = Math.abs(wrap(p.getYRot() - rotations[0]));
			if (diff >= angleMin.getValue() && diff <= angleMax.getValue() && diff < bestAngle) {
				bestAngle = diff;
				best = living;
			}
		}
		if (best == null) {
			return;
		}
		float[] rotations = rotationsTo(p, best);
		float yawStep = (float) (horizontal.getValue() * 3.0);
		float pitchStep = (float) (vertical.getValue() * 3.0);
		if (strafe.getValue() && p.input.getMoveVector().x != 0) {
			yawStep *= 1.25;
		}
		p.setYRot(p.getYRot() + Math.max(-yawStep, Math.min(yawStep, wrap(rotations[0] - p.getYRot()))));
		p.setXRot(p.getXRot() + Math.max(-pitchStep, Math.min(pitchStep, rotations[1] - p.getXRot())));
		if (legitAura.getValue() && timer.hasTimeElapsed(100, true) && p.distanceTo(best) <= range.getValue()) {
			mc.gameMode.attack(p, best);
			if (!noSwing.getValue()) {
				p.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
			}
			if (block.getValue() && p.getOffhandItem().isEmpty()) {
				// nothing to block with
			}
		}
	}

	private static float wrap(float angle) {
		angle %= 360;
		if (angle >= 180) {
			angle -= 360;
		}
		if (angle < -180) {
			angle += 360;
		}
		return angle;
	}

	private static float[] rotationsTo(Player player, LivingEntity target) {
		Vec3 eye = player.getEyePosition();
		Vec3 pos = target.position().add(0, target.getBbHeight() * 0.8, 0);
		double dx = pos.x - eye.x;
		double dy = pos.y - eye.y;
		double dz = pos.z - eye.z;
		double horizontal = Math.sqrt(dx * dx + dz * dz);
		return new float[] {(float) Math.toDegrees(Math.atan2(dz, dx)) - 90f,
			(float) -Math.toDegrees(Math.atan2(dy, horizontal))};
	}
}
