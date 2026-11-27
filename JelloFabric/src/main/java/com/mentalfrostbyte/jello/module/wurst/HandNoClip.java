package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Wurst HandNoClip: reach containers through walls.
 *
 * <p>Wurst does this by mixing into {@code BlockStateBase.getShape} and removing the collision
 * shape of non-listed blocks. Without that mixin this safe subset walks the look vector, ignores
 * walls and right-clicks the first matching block. Full port needs the BlockStateBase mixin.</p>
 */
public class HandNoClip extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 4.5, 1, 8, 0.5));
	private final ModeValue mode = addValue(new ModeValue("Mode", "Containers", "Containers", "All Blocks"));

	private final TimerUtil timer = new TimerUtil();

	public HandNoClip() {
		super("Hand No Clip", 0, Category.WORLD);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null
			|| mc.gui.screen() != null) {
			return;
		}
		if (!mc.options.keyUse.isDown() || mc.player.isShiftKeyDown()) {
			return;
		}
		if (!timer.hasTimeElapsed(200, true)) {
			return;
		}
		Vec3 eye = mc.player.getEyePosition();
		Vec3 direction = Vec3.directionFromRotation(mc.player.getXRot(), mc.player.getYRot());
		BlockPos previous = null;
		for (double step = 0.5; step <= range.getValue(); step += 0.25) {
			BlockPos pos = BlockPos.containing(eye.add(direction.scale(step)));
			if (pos.equals(previous)) {
				continue;
			}
			previous = pos;
			BlockState state = mc.level.getBlockState(pos);
			if (state.isAir()) {
				continue;
			}
			if (mode.is("Containers") && !state.hasBlockEntity()) {
				continue;
			}
			if (mode.is("All Blocks") && state.getCollisionShape(mc.level, pos).isEmpty()) {
				continue;
			}
			// The normal crosshair ray already reaches this block: let vanilla handle it.
			if (mc.hitResult instanceof BlockHitResult hit && hit.getBlockPos().equals(pos)) {
				return;
			}
			Direction face = facing(pos);
			Vec3 center = Vec3.atCenterOf(pos).add(face.getStepX() * 0.5, face.getStepY() * 0.5,
				face.getStepZ() * 0.5);
			mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND,
				new BlockHitResult(center, face, pos, false));
			mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
			return;
		}
	}

	private Direction facing(BlockPos pos) {
		Vec3 eye = mc.player.getEyePosition();
		Vec3 center = Vec3.atCenterOf(pos);
		double dx = eye.x - center.x;
		double dy = eye.y - center.y;
		double dz = eye.z - center.z;
		if (Math.abs(dx) > Math.abs(dz)) {
			return dx > 0 ? Direction.EAST : Direction.WEST;
		}
		if (Math.abs(dy) > Math.abs(dz)) {
			return dy > 0 ? Direction.UP : Direction.DOWN;
		}
		return dz > 0 ? Direction.SOUTH : Direction.NORTH;
	}
}
