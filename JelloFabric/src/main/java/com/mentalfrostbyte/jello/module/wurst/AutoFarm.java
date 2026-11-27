package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Wurst AutoFarm: harvests nearby mature crops and optionally replants them. */
public class AutoFarm extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 4.5, 1, 6, 0.05));
	private final BooleanValue replant = addValue(new BooleanValue("Replant", true));

	private BlockPos target;

	public AutoFarm() {
		super("Auto Farm", 0, Category.WORLD);
	}

	@Override
	public void onDisable() {
		if (mc.gameMode != null && mc.gameMode.isDestroying()) {
			mc.gameMode.stopDestroyBlock();
		}
		target = null;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		if (target != null) {
			BlockState state = mc.level.getBlockState(target);
			if (state.isAir()) {
				if (replant.getValue()) {
					replantAt(target);
				}
				target = null;
				return;
			}
			if (!isMature(state)) {
				target = null;
			} else {
				lookAt(Vec3.atCenterOf(target));
				if (!mc.gameMode.isDestroying()) {
					mc.gameMode.startDestroyBlock(target, Direction.UP);
				} else {
					mc.gameMode.continueDestroyBlock(target, Direction.UP);
				}
				return;
			}
		}
		target = findCrop();
	}

	private BlockPos findCrop() {
		double rangeSq = range.getValue() * range.getValue();
		int radius = (int) Math.ceil(range.getValue());
		BlockPos origin = mc.player.blockPosition();
		BlockPos best = null;
		double bestDistance = Double.MAX_VALUE;
		for (int x = -radius; x <= radius; x++) {
			for (int y = -3; y <= 3; y++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos pos = origin.offset(x, y, z);
					BlockState state = mc.level.getBlockState(pos);
					if (!isMature(state)) {
						continue;
					}
					double distance = Vec3.atCenterOf(pos).distanceToSqr(mc.player.getEyePosition());
					if (distance > rangeSq || distance >= bestDistance) {
						continue;
					}
					bestDistance = distance;
					best = pos.immutable();
				}
			}
		}
		return best;
	}

	private static boolean isMature(BlockState state) {
		if (state.getBlock() instanceof CropBlock crop) {
			return crop.isMaxAge(state);
		}
		if (state.getBlock() instanceof NetherWartBlock) {
			return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
		}
		return false;
	}

	private void replantAt(BlockPos pos) {
		BlockPos soil = pos.below();
		BlockState soilState = mc.level.getBlockState(soil);
		if (soilState.isAir() || !soilState.getFluidState().isEmpty()) {
			return;
		}
		Vec3 hit = Vec3.atCenterOf(soil).add(0, 0.5, 0);
		mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND,
			new BlockHitResult(hit, Direction.UP, soil, false));
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
