package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.util.TimerUtil;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Meteor Client ghost-hand: opens containers through walls. */
public class GhostHand extends Module {
	private final Set<BlockPos> visited = new HashSet<>();
	private final TimerUtil timer = new TimerUtil();

	public GhostHand() {
		super("Ghost Hand", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gui.screen() != null) {
			return;
		}
		if (!mc.options.keyUse.isDown() || mc.player.isShiftKeyDown()) {
			return;
		}
		if (!timer.hasTimeElapsed(150, true)) {
			return;
		}

		double reach = mc.player.blockInteractionRange();
		HitResult normal = mc.player.pick(reach, 1.0F, false);
		if (normal.getType() == HitResult.Type.BLOCK && mc.level
			.getBlockState(((BlockHitResult) normal).getBlockPos()).hasBlockEntity()) {
			return;
		}

		Vec3 direction = new Vec3(0, 0, 0.1)
			.xRot(-(float) Math.toRadians(mc.player.getXRot()))
			.yRot(-(float) Math.toRadians(mc.player.getYRot()));

		visited.clear();
		Vec3 eye = mc.player.getEyePosition();
		for (int i = 1; i < reach * 10; i++) {
			BlockPos pos = BlockPos.containing(eye.add(direction.scale(i)));
			if (!visited.add(pos)) {
				continue;
			}
			if (!mc.level.getBlockState(pos).hasBlockEntity()) {
				continue;
			}
			Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			for (InteractionHand hand : InteractionHand.values()) {
				InteractionResult result = mc.gameMode.useItemOn(mc.player, hand,
					new BlockHitResult(center, Direction.UP, pos, true));
				if (result.consumesAction()) {
					return;
				}
			}
		}
	}
}
