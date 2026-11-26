package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.Vec3;

/** Attack aura: targets the closest valid entity in reach, with smoothed aim. */
public class KillAura extends Module {
	public static boolean enabled;
	public static boolean attacking;
	public static LivingEntity closestE;
	public static float progressYaw;
	public static float progressPitch;

	private final NumberValue range = addValue(new NumberValue("Reach", 3.6, 1.0, 7.0, 0.1));
	private final BooleanValue autoblock = addValue(new BooleanValue("BlockHit", false));
	private final BooleanValue knockback = addValue(new BooleanValue("Extra Knockback", false));
	private final BooleanValue players = addValue(new BooleanValue("Attack Players", true));
	private final BooleanValue animals = addValue(new BooleanValue("Attack Mobs", false));
	private final BooleanValue mobs = addValue(new BooleanValue("Attack Passives", false));
	private final BooleanValue team = addValue(new BooleanValue("Team", false));
	private final TimerUtil timer = new TimerUtil();
	private float yaw;
	private float pitch;

	public KillAura() {
		super("Kill Aura", InputConstants.KEY_X, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		enabled = true;
		if (mc.player != null) {
			progressYaw = mc.player.getYRot();
			yaw = progressYaw;
			progressPitch = mc.player.getXRot();
			pitch = progressPitch;
		}
	}

	@Override
	public void onDisable() {
		enabled = false;
		attacking = false;
		closestE = null;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		LocalPlayer player = mc.player;
		if (player.tickCount < 20) {
			toggle();
			return;
		}
		closestE = findClosest();
		if (closestE == null) {
			attacking = false;
			progressYaw = player.getYRot();
			progressPitch = player.getXRot();
			return;
		}
		attacking = true;
		float[] rotations = rotationsTo(player, closestE);
		yaw = rotations[0];
		pitch = rotations[1];
		progressYaw += (yaw - progressYaw) / 3f + 0.1f;
		progressPitch += (pitch - progressPitch) / 3f + 0.1f;
		player.setYRot(progressYaw);
		player.setXRot(progressPitch);

		if (timer.hasTimeElapsed(100, true) && player.distanceTo(closestE) <= range.getValue()) {
			mc.gameMode.attack(player, closestE);
			player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
		}
	}

	private LivingEntity findClosest() {
		if (mc.level == null || mc.player == null) {
			return null;
		}
		LivingEntity best = null;
		double bestDistance = Double.MAX_VALUE;
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof LivingEntity living) || !isValid(living)) {
				continue;
			}
			double distance = living.distanceTo(mc.player);
			if (distance < bestDistance) {
				bestDistance = distance;
				best = living;
			}
		}
		return best;
	}

	private boolean isValid(LivingEntity entity) {
		LocalPlayer player = mc.player;
		if (player == null || entity == player || entity.isDeadOrDying() || entity.isInvisible()) {
			return false;
		}
		if (entity instanceof Player && !players.getValue()) {
			return false;
		}
		if (entity instanceof Animal && !animals.getValue()) {
			return false;
		}
		if (entity instanceof Monster && !mobs.getValue()) {
			return false;
		}
		if (!(entity instanceof Player) && !(entity instanceof Animal) && !(entity instanceof Monster)) {
			return false;
		}
		if (!isOnTeam(entity)) {
			return false;
		}
		double distance = entity.distanceTo(player);
		double reach = range.getValue();
		if (distance > reach) {
			return false;
		}
		return player.hasLineOfSight(entity) || distance <= 3.0;
	}

	private boolean isOnTeam(LivingEntity entity) {
		if (!team.getValue() || mc.player == null) {
			return true;
		}
		return mc.player.getTeam() == entity.getTeam();
	}

	private static float[] rotationsTo(Player player, LivingEntity target) {
		Vec3 eye = player.getEyePosition();
		Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.8, 0);
		double dx = targetPos.x - eye.x;
		double dy = targetPos.y - eye.y;
		double dz = targetPos.z - eye.z;
		double horizontal = Math.sqrt(dx * dx + dz * dz);
		float newYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
		float newPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
		return new float[] {newYaw, newPitch};
	}

	/** Keeps the aim smooth on the outgoing movement packets, like the original EventMotion handler. */
	@EventTarget
	public void onPacket(EventPacketSent event) {
		if (!attacking || closestE == null || !(event.getPacket() instanceof ServerboundMovePlayerPacket)) {
			return;
		}
		LocalPlayer p = mc.player;
		if (p == null) {
			return;
		}
		event.setPacket(new ServerboundMovePlayerPacket.PosRot(p.getX(), p.getY(), p.getZ(),
			progressYaw, progressPitch, p.onGround(), p.horizontalCollision));
	}
}
