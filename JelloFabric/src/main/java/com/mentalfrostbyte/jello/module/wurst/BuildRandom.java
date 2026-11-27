package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Wurst BuildRandom: right-clicks random positions around the player. */
public class BuildRandom extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 5, 1, 6, 0.05));
	private final NumberValue maxAttempts = addValue(new NumberValue("Max Attempts", 128, 1, 1024, 1));
	private final BooleanValue checkItem = addValue(new BooleanValue("Check Held Item", true));
	private final BooleanValue checkLos = addValue(new BooleanValue("Check Line Of Sight", false));

	private final Random random = new Random();

	public BuildRandom() {
		super("Build Random", 0, Category.WORLD);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		if (checkItem.getValue() && !mc.player.isHolding(stack -> !stack.isEmpty()
			&& stack.getItem() instanceof BlockItem)) {
			return;
		}
		int blockRange = (int) Math.ceil(range.getValue());
		int bound = blockRange * 2 + 1;
		BlockPos origin = BlockPos.containing(mc.player.getEyePosition());
		for (int i = 0; i < (int) maxAttempts.getValue(); i++) {
			BlockPos pos = origin.offset(random.nextInt(bound) - blockRange,
				random.nextInt(bound) - blockRange, random.nextInt(bound) - blockRange);
			if (tryPlace(pos)) {
				return;
			}
		}
	}

	private boolean tryPlace(BlockPos pos) {
		if (!mc.level.getBlockState(pos).canBeReplaced()) {
			return false;
		}
		double rangeSq = range.getValue() * range.getValue();
		if (mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) > rangeSq) {
			return false;
		}
		for (Direction direction : Direction.values()) {
			BlockPos support = pos.relative(direction);
			BlockState state = mc.level.getBlockState(support);
			if (state.canBeReplaced() || !state.getFluidState().isEmpty()) {
				continue;
			}
			Direction face = direction.getOpposite();
			Vec3 hit = Vec3.atCenterOf(support).add(face.getStepX() * 0.5, face.getStepY() * 0.5,
				face.getStepZ() * 0.5);
			if (checkLos.getValue() && !hasLineOfSight(support)) {
				continue;
			}
			mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND,
				new BlockHitResult(hit, face, support, false));
			mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
			return true;
		}
		return false;
	}

	private boolean hasLineOfSight(BlockPos pos) {
		BlockHitResult hit = mc.level.clip(new ClipContext(mc.player.getEyePosition(), Vec3.atCenterOf(pos),
			ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player));
		return hit.getType() == HitResult.Type.MISS || hit.getBlockPos().equals(pos);
	}
}
