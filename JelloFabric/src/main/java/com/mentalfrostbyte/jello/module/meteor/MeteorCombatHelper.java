package com.mentalfrostbyte.jello.module.meteor;

import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Shared placement/target helpers for the ported Meteor combat modules. */
final class MeteorCombatHelper {
	static final Minecraft mc = Minecraft.getInstance();

	private MeteorCombatHelper() {
	}

	/** Closest or lowest-health player within range, excluding the local player and spectators. */
	static Player findTarget(double range, boolean lowestHealth) {
		if (mc.player == null || mc.level == null) {
			return null;
		}
		Player best = null;
		double bestValue = Double.MAX_VALUE;
		for (Player player : mc.level.players()) {
			if (player == mc.player || player.isDeadOrDying() || player.isCreative() || player.isSpectator()) {
				continue;
			}
			double distance = player.distanceToSqr(mc.player);
			if (distance > range * range) {
				continue;
			}
			double value = lowestHealth ? player.getHealth() : distance;
			if (value < bestValue) {
				bestValue = value;
				best = player;
			}
		}
		return best;
	}

	/** First hotbar slot containing a block that matches the filter. */
	static int findBlockSlot(Predicate<Block> filter) {
		if (mc.player == null) {
			return -1;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (stack.getItem() instanceof BlockItem blockItem && filter.test(blockItem.getBlock())) {
				return i;
			}
		}
		return -1;
	}

	/** Hotbar slot (and optionally main inventory/offhand) containing the given item. */
	static int findItemSlot(Item item, boolean hotbarOnly) {
		if (mc.player == null) {
			return -1;
		}
		for (int i = 0; i < 9; i++) {
			if (mc.player.getInventory().getItem(i).is(item)) {
				return i;
			}
		}
		if (hotbarOnly) {
			return -1;
		}
		for (int i = 9; i < 36; i++) {
			if (mc.player.getInventory().getItem(i).is(item)) {
				return i;
			}
		}
		if (mc.player.getInventory().getItem(40).is(item)) {
			return 40;
		}
		return -1;
	}

	static boolean isReplaceable(BlockPos pos) {
		return mc.level != null && mc.level.getBlockState(pos).canBeReplaced();
	}

	static boolean withinReach(BlockPos pos, double range) {
		return mc.player != null && Vec3.atCenterOf(pos).distanceToSqr(mc.player.getEyePosition()) <= range * range;
	}

	/**
	 * Places one block at {@code pos} by clicking a neighbouring solid block.
	 * Returns true when the interaction was accepted by the client game mode.
	 */
	static boolean place(BlockPos pos, int slot, boolean rotate, boolean swing) {
		if (mc.player == null || mc.level == null || mc.gameMode == null || slot < 0) {
			return false;
		}
		if (!mc.level.getBlockState(pos).canBeReplaced()) {
			return false;
		}

		BlockPos support = null;
		Direction face = null;
		Vec3 hit = null;
		for (Direction direction : Direction.values()) {
			BlockPos neighbour = pos.relative(direction);
			BlockState state = mc.level.getBlockState(neighbour);
			if (state.canBeReplaced() || state.getCollisionShape(mc.level, neighbour).isEmpty()) {
				continue;
			}
			support = neighbour;
			face = direction.getOpposite();
			hit = Vec3.atCenterOf(neighbour).add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
			break;
		}
		if (support == null) {
			return false;
		}

		selectSlot(slot);
		if (rotate) {
			float[] rotations = rotationsTo(hit);
			mc.player.setYRot(rotations[0]);
			mc.player.setXRot(rotations[1]);
		}

		InteractionResult result = mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND,
			new BlockHitResult(hit, face, support, false));
		if (swing) {
			mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
		}
		return result.consumesAction();
	}

	static void selectSlot(int slot) {
		if (mc.player == null || slot < 0 || slot > 8) {
			return;
		}
		if (mc.player.getInventory().getSelectedSlot() != slot) {
			mc.player.getInventory().setSelectedSlot(slot);
			if (mc.getConnection() != null) {
				mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
			}
		}
	}

	static float[] rotationsTo(Vec3 hit) {
		Vec3 eye = mc.player.getEyePosition();
		double dx = hit.x - eye.x;
		double dy = hit.y - eye.y;
		double dz = hit.z - eye.z;
		float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
		float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
		return new float[] {yaw, pitch};
	}

	static void centerPlayer() {
		if (mc.player == null) {
			return;
		}
		BlockPos pos = mc.player.blockPosition();
		double x = pos.getX() + 0.5;
		double z = pos.getZ() + 0.5;
		if (Math.abs(mc.player.getX() - x) > 0.001 || Math.abs(mc.player.getZ() - z) > 0.001) {
			mc.player.setPos(x, mc.player.getY(), z);
		}
	}

	/** Meteor treats explosion-resistant blocks as hole walls. */
	static boolean isHard(Block block) {
		String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
		return path.contains("obsidian")
			|| path.contains("bedrock")
			|| path.contains("netherite")
			|| path.contains("anvil")
			|| path.contains("respawn_anchor")
			|| path.contains("ender_chest")
			|| path.contains("enchanting_table")
			|| path.contains("ancient_debris")
			|| block.defaultDestroyTime() >= 20.0f;
	}

	static boolean inHole(BlockPos feet) {
		if (mc.level == null) {
			return false;
		}
		if (!mc.level.getBlockState(feet).canBeReplaced() || !mc.level.getBlockState(feet.above()).canBeReplaced()) {
			return false;
		}
		if (!isHard(mc.level.getBlockState(feet.below()).getBlock())) {
			return false;
		}
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			if (!isHard(mc.level.getBlockState(feet.relative(direction)).getBlock())) {
				return false;
			}
		}
		return true;
	}
}
