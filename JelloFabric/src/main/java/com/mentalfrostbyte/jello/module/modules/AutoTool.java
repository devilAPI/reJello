package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** Selects the fastest hotbar tool for the block being mined. */
public class AutoTool extends Module {
	public AutoTool() {
		super("AutoTool", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || !mc.options.keyAttack.isDown()) {
			return;
		}
		HitResult hit = mc.hitResult;
		if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
			return;
		}
		BlockPos pos = blockHit.getBlockPos();
		BlockState state = mc.level.getBlockState(pos);
		int best = -1;
		float bestSpeed = 1.0f;
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			float speed = stack.getDestroySpeed(state);
			if (speed > bestSpeed) {
				bestSpeed = speed;
				best = i;
			}
		}
		if (best != -1 && mc.player.getInventory().getSelectedSlot() != best) {
			mc.player.getInventory().setSelectedSlot(best);
		}
	}
}
