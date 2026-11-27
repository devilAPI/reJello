package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Excavator: Meteor uses a Baritone selection. Baritone is not part of this client, so the adapted version
 * clears a configurable box around the player.
 */
public class Excavator extends Module {
	private final NumberValue radius = addValue(new NumberValue("Radius", 3, 1, 8, 1));
	private final NumberValue depth = addValue(new NumberValue("Depth", 3, 1, 8, 1));
	private final NumberValue delay = addValue(new NumberValue("Delay", 0, 0, 20, 1));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));
	private final BooleanValue keepActive = addValue(new BooleanValue("Keep Active", false));

	private int tick;
	private int idleTicks;
	private BlockPos lastMined;

	public Excavator() {
		super("Excavator", 0, Category.WORLD);
	}

	@Override
	public void onDisable() {
		tick = 0;
		idleTicks = 0;
		lastMined = null;
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

		if (mineNext()) {
			idleTicks = 0;
			return;
		}
		if (++idleTicks > 40 && !keepActive.getValue()) {
			setToggled(false);
		}
	}

	private boolean mineNext() {
		BlockPos origin = mc.player.blockPosition();
		int r = (int) radius.getValue();
		int d = (int) depth.getValue();

		BlockPos best = null;
		double bestDistance = Double.MAX_VALUE;
		for (int y = 0; y <= d; y++) {
			for (int x = -r; x <= r; x++) {
				for (int z = -r; z <= r; z++) {
					BlockPos pos = origin.offset(x, -y, z);
					BlockState state = mc.level.getBlockState(pos);
					if (state.isAir() || !state.getFluidState().isEmpty()
						|| state.getDestroySpeed(mc.level, pos) < 0) {
						continue;
					}
					double distance = Vec3.atCenterOf(pos).distanceToSqr(mc.player.getEyePosition());
					double range = mc.player.blockInteractionRange();
					if (distance > range * range) {
						continue;
					}
					if (distance < bestDistance) {
						bestDistance = distance;
						best = pos;
					}
				}
			}
		}

		if (best == null) {
			return false;
		}
		if (!best.equals(lastMined)) {
			lastMined = best;
			mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
		}
		if (rotate.getValue()) {
			lookAt(best);
		}
		mc.gameMode.continueDestroyBlock(best, Direction.UP);
		return true;
	}

	private void lookAt(BlockPos pos) {
		Vec3 eye = mc.player.getEyePosition();
		Vec3 target = Vec3.atCenterOf(pos);
		double dx = target.x - eye.x;
		double dy = target.y - eye.y;
		double dz = target.z - eye.z;
		mc.player.setYRot((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
		mc.player.setXRot((float) (-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)))));
	}
}
