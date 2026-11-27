package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventJoinServer;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.Vec3;

/** Attack aura, ported from Sigma Reborn 1.18 with its setting set. */
public class KillAura extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	public static boolean enabled;
	public static boolean attacking;
	public static LivingEntity target;
	public static LivingEntity closestE;
	public static float progressYaw;
	public static float progressPitch;

	private final NumberValue range = addValue(new NumberValue("Range", 3.0, 1.0, 6.0, 0.1));
	private final NumberValue aps = addValue(new NumberValue("APS", 10, 1, 20, 1));
	private final ModeValue autoBlock = addValue(new ModeValue("AutoBlock Mode", "Normal", "Vanilla", "Normal", "Packet", "Packet2", "Packet3", "Legit"));
	private final BooleanValue autoBlockToggle = addValue(new BooleanValue("AutoBlock", true));
	private final BooleanValue players = addValue(new BooleanValue("Attack Players", true));
	private final BooleanValue mobs = addValue(new BooleanValue("Attack Mobs", false));
	private final BooleanValue attackTeam = addValue(new BooleanValue("AttackTeam", false));
	private final ModeValue targetEsp = addValue(new ModeValue("TargetESP", "Classic", "Classic", "Rainbow", "None"));
	private final ModeValue rotationMode = addValue(new ModeValue("Rotation Mode", "Simple", "Simple", "Lock", "MatrixOld", "None", "Verus"));
	private final NumberValue randomness = addValue(new NumberValue("Randomness", 9, 1, 40, 0.1));
	private final BooleanValue raytrace = addValue(new BooleanValue("RayCast", false));
	private final BooleanValue strafe = addValue(new BooleanValue("Strafe", false));
	private final BooleanValue disableOnWorldChange = addValue(new BooleanValue("DisableOnWorldChange", true));

	private final TimerUtil timer = new TimerUtil();
	private float yaw;
	private float pitch;

	public KillAura() {
		super("KillAura", InputConstants.KEY_R, Category.COMBAT);
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
		target = null;
		closestE = null;
	}

	@EventTarget
	public void onJoin(EventJoinServer event) {
		if (disableOnWorldChange.getValue()) {
			setToggled(false);
		}
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		LocalPlayer player = mc.player;
		target = findClosest();
		closestE = target;
		if (target == null) {
			attacking = false;
			progressYaw = player.getYRot();
			progressPitch = player.getXRot();
			return;
		}
		attacking = true;
		if (!rotationMode.is("None")) {
			float[] rotations = rotationsTo(player, target);
			yaw = rotations[0];
			pitch = rotations[1];
			float speed = rotationMode.is("Lock") ? 1f : 3f;
			progressYaw += (yaw - progressYaw) / speed;
			progressPitch += (pitch - progressPitch) / speed;
			if (!strafe.getValue()) {
				player.setYRot(progressYaw);
				player.setXRot(progressPitch);
			}
		}

		long interval = (long) (1000.0 / Math.max(1, aps.getValue()));
		if (timer.hasTimeElapsed(interval, true) && player.distanceTo(target) <= range.getValue()) {
			mc.gameMode.attack(player, target);
			player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
			if (autoBlockToggle.getValue() && !autoBlock.is("Vanilla") && player.getOffhandItem().get(DataComponents.EQUIPPABLE) != null) {
				mc.gameMode.useItem(player, InteractionHand.OFF_HAND);
			}
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
		if (entity instanceof Player) {
			if (!players.getValue()) {
				return false;
			}
			if (!attackTeam.getValue() && player.getTeam() == entity.getTeam()) {
				return false;
			}
		} else if (entity instanceof Monster) {
			if (!mobs.getValue()) {
				return false;
			}
		} else {
			return false;
		}
		if (AntiBot.isBot(entity)) {
			return false;
		}
		double distance = entity.distanceTo(player);
		if (distance > range.getValue()) {
			return false;
		}
		return !raytrace.getValue() || player.hasLineOfSight(entity) || distance <= 3.0;
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

	/** Replaces outgoing movement packets with the smoothed rotation, like the 1.18 EventMotion handler. */
	@EventTarget
	public void onPacket(EventPacketSent event) {
		if (!attacking || target == null || rotationMode.is("None") || !(event.getPacket() instanceof ServerboundMovePlayerPacket)) {
			return;
		}
		LocalPlayer p = mc.player;
		if (p == null) {
			return;
		}
		event.setPacket(new ServerboundMovePlayerPacket.PosRot(p.getX(), p.getY(), p.getZ(),
			progressYaw, progressPitch, p.onGround(), p.horizontalCollision));
	}

	/** Used by the ESP/TargetHUD modules. */
	public static LivingEntity getTarget() {
		return target;
	}
}
