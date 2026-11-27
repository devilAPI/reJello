package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Wurst InstaBuild: places a whole template in a single tick.
 *
 * <p>Same template-system limitation as {@link AutoBuild}; the safe subset instantly builds a
 * small platform, wall or pillar in front of the player. No mixin required.</p>
 */
public class InstaBuild extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 6, 1, 10, 0.05));
	private final ModeValue mode = addValue(new ModeValue("Mode", "Platform", "Platform", "Wall", "Pillar"));

	public InstaBuild() {
		super("Insta Build", 0, Category.WORLD);
	}

	@Override
	public void onEnable() {
		if (mc.player == null || mc.level == null || mc.gameMode == null) {
			setToggled(false);
			return;
		}
		for (BlockPos pos : buildPositions()) {
			placeAt(pos);
		}
		setToggled(false);
	}

	private List<BlockPos> buildPositions() {
		Direction facing = mc.player.getDirection();
		Direction side = facing.getClockWise();
		List<BlockPos> positions = new ArrayList<>();
		switch (mode.getMode()) {
			case "Wall" -> {
				BlockPos base = mc.player.blockPosition().relative(facing, 3);
				for (int dx = -2; dx <= 2; dx++) {
					for (int dy = 0; dy <= 2; dy++) {
						positions.add(base.relative(side, dx).above(dy));
					}
				}
			}
			case "Pillar" -> {
				BlockPos base = mc.player.blockPosition().relative(facing, 2);
				for (int dy = 0; dy <= 3; dy++) {
					positions.add(base.above(dy));
				}
			}
			default -> {
				BlockPos base = mc.player.blockPosition().below().relative(facing, 2);
				for (int dx = -2; dx <= 2; dx++) {
					for (int dz = 0; dz <= 2; dz++) {
						positions.add(base.relative(side, dx).relative(facing, dz));
					}
				}
			}
		}
		positions.removeIf(pos -> mc.player.getBoundingBox().intersects(new AABB(pos)));
		return positions;
	}

	private void placeAt(BlockPos pos) {
		if (!mc.level.getBlockState(pos).canBeReplaced()) {
			return;
		}
		double rangeSq = range.getValue() * range.getValue();
		if (mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) > rangeSq) {
			return;
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
			mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND,
				new BlockHitResult(hit, face, support, false));
			mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
			return;
		}
	}
}
