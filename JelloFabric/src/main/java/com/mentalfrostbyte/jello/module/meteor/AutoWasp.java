package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMove;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Meteor Client auto-wasp: flies at another player with your elytra. */
public class AutoWasp extends Module {
	private final NumberValue horizontalSpeed = addValue(new NumberValue("Horizontal Speed", 2.0, 0.0, 10.0, 0.1));
	private final NumberValue verticalSpeed = addValue(new NumberValue("Vertical Speed", 3.0, 0.0, 10.0, 0.1));
	private final BooleanValue avoidLanding = addValue(new BooleanValue("Avoid Landing", true));
	private final BooleanValue predictMovement = addValue(new BooleanValue("Predict Movement", true));
	private final BooleanValue onlyFriends = addValue(new BooleanValue("Only Friends", false));
	private final ModeValue action = addValue(new ModeValue("Action On Target Loss", "Toggle", "Toggle", "Choose New Target", "Disconnect"));

	public Player target;
	private int jumpTimer;
	private boolean incrementJumpTimer;

	public AutoWasp() {
		super("Auto Wasp", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		if (target == null || target.isRemoved()) {
			target = findTarget();
		}
		jumpTimer = 0;
		incrementJumpTimer = false;
	}

	@Override
	public void onDisable() {
		target = null;
		incrementJumpTimer = false;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (target == null) {
			target = findTarget();
			if (target == null) {
				setToggled(false);
				return;
			}
		}
		if (target.isRemoved() || target.isDeadOrDying()) {
			switch (action.getMode()) {
				case "Choose New Target" -> target = findTarget();
				case "Disconnect" -> {
					if (mc.getConnection() != null) {
						mc.getConnection().handleDisconnect(new ClientboundDisconnectPacket(
							Component.literal(ChatFormatting.GRAY + "[Auto Wasp] Lost target.")));
					}
					setToggled(false);
				}
				default -> setToggled(false);
			}
			if (!isToggled()) {
				return;
			}
		}
		if (target == null) {
			setToggled(false);
			return;
		}

		if (!mc.player.getItemBySlot(EquipmentSlot.CHEST).has(DataComponents.GLIDER)) {
			return;
		}

		if (incrementJumpTimer) {
			jumpTimer++;
		}
		if (!mc.player.isFallFlying()) {
			if (!incrementJumpTimer) {
				incrementJumpTimer = true;
			}
			if (mc.player.onGround()) {
				mc.player.jumpFromGround();
				return;
			}
			if (jumpTimer >= 4) {
				jumpTimer = 0;
				mc.player.setJumping(false);
				mc.player.setSprinting(true);
				if (mc.getConnection() != null) {
					mc.getConnection().send(new ServerboundPlayerCommandPacket(mc.player,
						ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
				}
			}
		} else {
			incrementJumpTimer = false;
			jumpTimer = 0;
		}
	}

	@EventTarget
	public void onMove(EventMove event) {
		if (!isToggled() || mc.player == null || mc.level == null || target == null) {
			return;
		}
		if (!mc.player.getItemBySlot(EquipmentSlot.CHEST).has(DataComponents.GLIDER) || !mc.player.isFallFlying()) {
			return;
		}

		Vec3 targetPos = target.position();
		if (predictMovement.getValue()) {
			targetPos = targetPos.add(target.getDeltaMovement());
		}

		if (avoidLanding.getValue()) {
			double d = target.getBoundingBox().getXsize() / 2.0;
			for (Direction dir : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
				Vec3 corner = targetPos.relative(dir, d).relative(dir.getClockWise(), d);
				BlockPos pos = BlockPos.containing(corner).below();
				if (!mc.level.getBlockState(pos).getCollisionShape(mc.level, pos).isEmpty()
					&& Math.abs(targetPos.y - (pos.getY() + 1)) <= 0.25) {
					targetPos = new Vec3(targetPos.x, pos.getY() + 1.25, targetPos.z);
					break;
				}
			}
		}

		double xDist = targetPos.x - mc.player.getX();
		double zDist = targetPos.z - mc.player.getZ();
		double absX = Math.abs(xDist);
		double absZ = Math.abs(zDist);

		double diagonal = 0;
		if (absX > 1.0E-5 && absZ > 1.0E-5) {
			diagonal = 1 / Math.sqrt(absX * absX + absZ * absZ);
		}

		double xVel = 0;
		double zVel = 0;
		if (absX > 1.0E-5) {
			xVel = absX < horizontalSpeed.getValue() ? xDist : horizontalSpeed.getValue() * Math.signum(xDist);
			if (diagonal != 0) {
				xVel *= absX * diagonal;
			}
		}
		if (absZ > 1.0E-5) {
			zVel = absZ < horizontalSpeed.getValue() ? zDist : horizontalSpeed.getValue() * Math.signum(zDist);
			if (diagonal != 0) {
				zVel *= absZ * diagonal;
			}
		}

		double yVel = 0;
		double yDist = targetPos.y - mc.player.getY();
		if (Math.abs(yDist) > 1.0E-5) {
			yVel = Math.abs(yDist) < verticalSpeed.getValue() ? yDist : verticalSpeed.getValue() * Math.signum(yDist);
		}

		event.x = xVel;
		event.y = yVel;
		event.z = zVel;
	}

	private Player findTarget() {
		if (mc.player == null || mc.level == null) {
			return null;
		}
		Player best = null;
		double bestDistance = Double.MAX_VALUE;
		for (Player player : mc.level.players()) {
			if (player == mc.player || player.isDeadOrDying() || player.getHealth() <= 0) {
				continue;
			}
			// No friends system in JelloFabric: only-friends falls back to every player.
			double distance = mc.player.distanceToSqr(player);
			if (distance < bestDistance) {
				bestDistance = distance;
				best = player;
			}
		}
		return best;
	}

	public boolean onlyFriends() {
		return isToggled() && onlyFriends.getValue();
	}
}
