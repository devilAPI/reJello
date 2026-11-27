package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Wurst BonemealAura: uses bone meal on nearby growable blocks. */
public class BonemealAura extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 5, 1, 6, 0.05));
	private final BooleanValue multiMeal = addValue(new BooleanValue("MultiMeal", false));

	public BonemealAura() {
		super("Bonemeal Aura", 0, Category.WORLD);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null
			|| mc.gui.screen() != null) {
			return;
		}
		if (!mc.player.isHolding(Items.BONE_MEAL)) {
			int slot = findBoneMeal();
			if (slot == -1) {
				return;
			}
			mc.player.getInventory().setSelectedSlot(slot);
			if (mc.getConnection() != null) {
				mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
			}
			return;
		}

		List<BlockPos> blocks = findBlocks();
		if (blocks.isEmpty()) {
			return;
		}
		int limit = multiMeal.getValue() ? blocks.size() : 1;
		for (int i = 0; i < limit; i++) {
			BlockPos pos = blocks.get(i);
			Vec3 center = Vec3.atCenterOf(pos);
			lookAt(center);
			mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND,
				new BlockHitResult(center, Direction.UP, pos, false));
		}
		mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
	}

	private int findBoneMeal() {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (!stack.isEmpty() && stack.is(Items.BONE_MEAL)) {
				return i;
			}
		}
		return -1;
	}

	private List<BlockPos> findBlocks() {
		double rangeSq = range.getValue() * range.getValue();
		int radius = (int) Math.ceil(range.getValue());
		BlockPos eyes = BlockPos.containing(mc.player.getEyePosition());
		List<BlockPos> blocks = new ArrayList<>();
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos pos = eyes.offset(x, y, z);
					BlockState state = mc.level.getBlockState(pos);
					if (state.isAir() || state.getBlock() instanceof GrassBlock
						|| !(state.getBlock() instanceof BonemealableBlock bonemealable)) {
						continue;
					}
					if (!bonemealable.isValidBonemealTarget(mc.level, pos, state, BonemealSource.INTERACTION)) {
						continue;
					}
					if (Vec3.atCenterOf(pos).distanceToSqr(mc.player.getEyePosition()) > rangeSq) {
						continue;
					}
					blocks.add(pos.immutable());
				}
			}
		}
		// Bone-mealing nearby plants first can hide the ones behind them, so go farthest first.
		blocks.sort(Comparator.comparingDouble((BlockPos pos) ->
			Vec3.atCenterOf(pos).distanceToSqr(mc.player.getEyePosition())).reversed());
		return blocks;
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
