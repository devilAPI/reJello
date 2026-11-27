package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Meteor Client liquid filler: places blocks into liquid source blocks around the player. */
public class LiquidFiller extends Module {
	private static final Set<Block> WHITELIST = Set.of(
		Blocks.DIRT, Blocks.COBBLESTONE, Blocks.STONE, Blocks.NETHERRACK,
		Blocks.DIORITE, Blocks.GRANITE, Blocks.ANDESITE);

	private final ModeValue placeIn = addValue(new ModeValue("Place In", "Both", "Both", "Water", "Lava"));
	private final ModeValue shape = addValue(new ModeValue("Shape", "Sphere", "Sphere", "Cube"));
	private final NumberValue placeRange = addValue(new NumberValue("Place Range", 4.5, 0, 8, 0.5));
	private final NumberValue wallsRange = addValue(new NumberValue("Walls Range", 4.5, 0, 8, 0.5));
	private final NumberValue delay = addValue(new NumberValue("Delay", 0, 0, 20, 1));
	private final NumberValue maxPerTick = addValue(new NumberValue("Max Blocks Per Tick", 1, 1, 10, 1));
	private final ModeValue sortMode = addValue(new ModeValue("Sort Mode", "Furthest",
		"Furthest", "Closest", "TopDown", "BottomUp", "None"));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));
	private final ModeValue listMode = addValue(new ModeValue("List Mode", "Whitelist", "Whitelist", "Blacklist"));

	private final List<BlockPos> candidates = new ArrayList<>();
	private int tick;

	public LiquidFiller() {
		super("Liquid Filler", 0, Category.WORLD);
	}

	@Override
	public void onDisable() {
		candidates.clear();
		tick = 0;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		if (tick < (int) delay.getValue()) {
			tick++;
			return;
		}
		tick = 0;

		int slot = findBlockSlot();
		if (slot == -1) {
			return;
		}

		candidates.clear();
		int r = (int) Math.ceil(placeRange.getValue());
		BlockPos origin = mc.player.blockPosition();
		double pX = mc.player.getX();
		double pY = mc.player.getY();
		double pZ = mc.player.getZ();

		for (int x = -r; x <= r; x++) {
			for (int y = -r; y <= r; y++) {
				for (int z = -r; z <= r; z++) {
					BlockPos pos = origin.offset(x, y, z);
					BlockState state = mc.level.getBlockState(pos);
					FluidState fluidState = state.getFluidState();
					if (!fluidState.isSource()) {
						continue;
					}
					Fluid fluid = fluidState.getType();
					if ((placeIn.is("Both") && fluid != Fluids.WATER && fluid != Fluids.LAVA)
						|| (placeIn.is("Water") && fluid != Fluids.WATER)
						|| (placeIn.is("Lava") && fluid != Fluids.LAVA)) {
						continue;
					}
					if (!state.canBeReplaced() || !hasSupport(pos) || isOutOfRange(pos)) {
						continue;
					}
					candidates.add(pos.immutable());
				}
			}
		}

		if (candidates.isEmpty()) {
			return;
		}

		if (sortMode.is("Closest") || sortMode.is("Furthest")) {
			double sign = sortMode.is("Closest") ? 1.0 : -1.0;
			candidates.sort(Comparator.comparingDouble((BlockPos pos) ->
				Vec3.atCenterOf(pos).distanceToSqr(pX, pY, pZ) * sign));
		} else if (sortMode.is("TopDown")) {
			candidates.sort(Comparator.comparingInt((BlockPos pos) -> pos.getY()).reversed());
		} else if (sortMode.is("BottomUp")) {
			candidates.sort(Comparator.comparingInt((BlockPos pos) -> pos.getY()));
		}

		swapTo(slot);
		int placed = 0;
		for (BlockPos pos : candidates) {
			if (placed >= (int) maxPerTick.getValue()) {
				break;
			}
			if (placeAt(pos)) {
				placed++;
			}
		}
	}

	private int findBlockSlot() {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (!(stack.getItem() instanceof BlockItem blockItem)) {
				continue;
			}
			boolean allowed = listMode.is("Whitelist")
				? WHITELIST.contains(blockItem.getBlock())
				: true;
			if (allowed) {
				return i;
			}
		}
		return -1;
	}

	private boolean placeAt(BlockPos pos) {
		for (Direction direction : Direction.values()) {
			BlockPos support = pos.relative(direction);
			BlockState supportState = mc.level.getBlockState(support);
			if (supportState.canBeReplaced() || !supportState.getFluidState().isEmpty()) {
				continue;
			}
			Direction face = direction.getOpposite();
			Vec3 hit = Vec3.atCenterOf(support).add(
				face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
			if (rotate.getValue()) {
				lookAt(hit);
			}
			mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND,
				new BlockHitResult(hit, face, support, false));
			mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
			return true;
		}
		return false;
	}

	private boolean hasSupport(BlockPos pos) {
		for (Direction direction : Direction.values()) {
			BlockState state = mc.level.getBlockState(pos.relative(direction));
			if (!state.canBeReplaced() && state.getFluidState().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private boolean isOutOfRange(BlockPos pos) {
		if (!withinShape(pos, placeRange.getValue())) {
			return true;
		}
		BlockHitResult result = mc.level.clip(new ClipContext(
			mc.player.getEyePosition(), Vec3.atCenterOf(pos),
			ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player));
		if (result == null || !result.getBlockPos().equals(pos)) {
			return !withinShape(pos, wallsRange.getValue());
		}
		return false;
	}

	private boolean withinShape(BlockPos pos, double range) {
		if (shape.is("Cube")) {
			BlockPos origin = mc.player.blockPosition();
			int dx = Math.abs(pos.getX() - origin.getX());
			int dy = Math.abs(pos.getY() - origin.getY());
			int dz = Math.abs(pos.getZ() - origin.getZ());
			return Math.max(Math.max(dx, dy), dz) <= Math.floor(range);
		}
		return Vec3.atCenterOf(pos).distanceTo(mc.player.getEyePosition()) <= range;
	}

	private void swapTo(int slot) {
		if (mc.player.getInventory().getSelectedSlot() == slot) {
			return;
		}
		mc.player.getInventory().setSelectedSlot(slot);
		if (mc.getConnection() != null) {
			mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
		}
	}

	private void lookAt(Vec3 target) {
		Vec3 eye = mc.player.getEyePosition();
		double dx = target.x - eye.x;
		double dy = target.y - eye.y;
		double dz = target.z - eye.z;
		mc.player.setYRot((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
		mc.player.setXRot((float) (-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)))));
	}
}
