package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Wurst InstantBunker: builds a 5x3x5 shell around the player in one go. */
public class InstantBunker extends Module {
	private static final int[][] TEMPLATE = {
		{2, 0, 2}, {-2, 0, 2}, {2, 0, -2}, {-2, 0, -2},
		{2, 1, 2}, {-2, 1, 2}, {2, 1, -2}, {-2, 1, -2},
		{2, 2, 2}, {-2, 2, 2}, {2, 2, -2}, {-2, 2, -2},
		{1, 2, 2}, {0, 2, 2}, {-1, 2, 2},
		{2, 2, 1}, {2, 2, 0}, {2, 2, -1},
		{-2, 2, 1}, {-2, 2, 0}, {-2, 2, -1},
		{1, 2, -2}, {0, 2, -2}, {-1, 2, -2},
		{1, 0, 2}, {0, 0, 2}, {-1, 0, 2},
		{2, 0, 1}, {2, 0, 0}, {2, 0, -1},
		{-2, 0, 1}, {-2, 0, 0}, {-2, 0, -1},
		{1, 0, -2}, {0, 0, -2}, {-1, 0, -2},
		{1, 1, 2}, {0, 1, 2}, {-1, 1, 2},
		{2, 1, 1}, {2, 1, 0}, {2, 1, -1},
		{-2, 1, 1}, {-2, 1, 0}, {-2, 1, -1},
		{1, 1, -2}, {0, 1, -2}, {-1, 1, -2},
		{1, 2, 1}, {-1, 2, 1}, {1, 2, -1}, {-1, 2, -1},
		{0, 2, 1}, {1, 2, 0}, {-1, 2, 0}, {0, 2, -1},
		{0, 2, 0}
	};

	private final ArrayList<BlockPos> positions = new ArrayList<>();
	private int startTimer;

	public InstantBunker() {
		super("Instant Bunker", 0, Category.WORLD);
	}

	@Override
	public void onEnable() {
		positions.clear();
		if (mc.player == null || mc.level == null) {
			return;
		}
		if (!mc.player.onGround()) {
			Jello.addChatMessage("Can't build this in mid-air.");
			setToggled(false);
			return;
		}
		ItemStack stack = mc.player.getMainHandItem();
		if (!(stack.getItem() instanceof BlockItem)) {
			Jello.addChatMessage("You must have blocks in the main hand.");
			setToggled(false);
			return;
		}
		if (stack.getCount() < 57 && !mc.player.getAbilities().instabuild) {
			Jello.addChatMessage("Not enough blocks. Bunker may be incomplete.");
		}

		BlockPos startPos = BlockPos.containing(mc.player.position());
		Direction facing = mc.player.getDirection();
		Direction facing2 = facing.getCounterClockWise();
		for (int[] pos : TEMPLATE) {
			positions.add(startPos.above(pos[1]).relative(facing, pos[2]).relative(facing2, pos[0]));
		}
		startTimer = 2;
		mc.player.jumpFromGround();
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		if (startTimer > 0) {
			startTimer--;
			return;
		}
		for (BlockPos pos : positions) {
			if (!mc.level.getBlockState(pos).canBeReplaced()
				|| mc.player.getBoundingBox().intersects(new AABB(pos))) {
				continue;
			}
			placeAt(pos);
		}
		mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
		if (mc.player.onGround()) {
			setToggled(false);
		}
	}

	private void placeAt(BlockPos pos) {
		Vec3 center = Vec3.atCenterOf(pos);
		for (Direction direction : Direction.values()) {
			BlockPos support = pos.relative(direction);
			BlockState state = mc.level.getBlockState(support);
			if (state.canBeReplaced() || !state.getFluidState().isEmpty()) {
				continue;
			}
			Direction face = direction.getOpposite();
			Vec3 hit = Vec3.atCenterOf(support).add(face.getStepX() * 0.5, face.getStepY() * 0.5,
				face.getStepZ() * 0.5);
			if (mc.player.getEyePosition().distanceToSqr(center) > 36) {
				continue;
			}
			mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND,
				new BlockHitResult(hit, face, support, false));
			return;
		}
	}
}
