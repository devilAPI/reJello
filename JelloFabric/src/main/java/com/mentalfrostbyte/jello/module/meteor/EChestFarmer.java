package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Meteor Client echest farmer: places and breaks ender chests for obsidian. */
public class EChestFarmer extends Module {
	private final BooleanValue selfToggle = addValue(new BooleanValue("Self Toggle", false));
	private final BooleanValue ignoreExisting = addValue(new BooleanValue("Ignore Existing", true));
	private final NumberValue amount = addValue(new NumberValue("Amount", 64, 8, 512, 1));
	private final BooleanValue swingHand = addValue(new BooleanValue("Swing Hand", true));

	private BlockPos target;
	private int startCount;
	private int originalSlot = -1;

	public EChestFarmer() {
		super("Echest Farmer", 0, Category.WORLD);
	}

	@Override
	public void onEnable() {
		target = null;
		startCount = countObsidian();
	}

	@Override
	public void onDisable() {
		restoreSlot();
		target = null;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}

		if (target == null) {
			if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) {
				return;
			}
			BlockPos pos = ((BlockHitResult) mc.hitResult).getBlockPos().above();
			BlockState state = mc.level.getBlockState(pos);
			if (!state.canBeReplaced() && state.getBlock() != Blocks.ENDER_CHEST) {
				return;
			}
			target = pos;
		}

		if (!withinReach(target)) {
			target = null;
			return;
		}

		if (selfToggle.getValue()) {
			int collected = countObsidian() - (ignoreExisting.getValue() ? startCount : 0);
			if (collected >= (int) amount.getValue()) {
				restoreSlot();
				setToggled(false);
				return;
			}
		}

		BlockState state = mc.level.getBlockState(target);
		if (state.getBlock() == Blocks.ENDER_CHEST) {
			int slot = bestTool();
			if (slot == -1) {
				return;
			}
			swapTo(slot);
			mc.gameMode.continueDestroyBlock(target, Direction.UP);
			return;
		}

		if (state.canBeReplaced()) {
			int slot = findEchest();
			if (slot == -1) {
				setToggled(false);
				return;
			}
			swapTo(slot);
			place(target);
		}
	}

	private boolean place(BlockPos pos) {
		for (Direction direction : Direction.values()) {
			BlockPos support = pos.relative(direction);
			BlockState supportState = mc.level.getBlockState(support);
			if (supportState.canBeReplaced() || !supportState.getFluidState().isEmpty()) {
				continue;
			}
			Direction face = direction.getOpposite();
			Vec3 hit = Vec3.atCenterOf(support).add(
				face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
			mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND,
				new BlockHitResult(hit, face, support, false));
			if (swingHand.getValue()) {
				mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
			}
			return true;
		}
		return false;
	}

	private int bestTool() {
		BlockState state = Blocks.ENDER_CHEST.defaultBlockState();
		int best = -1;
		float bestSpeed = 1.0f;
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (hasSilkTouch(stack)) {
				continue;
			}
			float speed = stack.getDestroySpeed(state);
			if (speed > bestSpeed) {
				bestSpeed = speed;
				best = i;
			}
		}
		return best;
	}

	private static boolean hasSilkTouch(ItemStack stack) {
		ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		for (Holder<Enchantment> enchantment : enchantments.keySet()) {
			if (enchantment.is(Enchantments.SILK_TOUCH)) {
				return true;
			}
		}
		return false;
	}

	private int findEchest() {
		for (int i = 0; i < 9; i++) {
			if (mc.player.getInventory().getItem(i).getItem() == Items.ENDER_CHEST) {
				return i;
			}
		}
		return -1;
	}

	private int countObsidian() {
		int count = 0;
		for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (stack.getItem() == Items.OBSIDIAN) {
				count += stack.getCount();
			}
		}
		return count;
	}

	private void swapTo(int slot) {
		int selected = mc.player.getInventory().getSelectedSlot();
		if (selected == slot) {
			return;
		}
		if (originalSlot == -1) {
			originalSlot = selected;
		}
		mc.player.getInventory().setSelectedSlot(slot);
		if (mc.getConnection() != null) {
			mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
		}
	}

	private void restoreSlot() {
		if (originalSlot == -1) {
			return;
		}
		mc.player.getInventory().setSelectedSlot(originalSlot);
		if (mc.getConnection() != null) {
			mc.getConnection().send(new ServerboundSetCarriedItemPacket(originalSlot));
		}
		originalSlot = -1;
	}

	private boolean withinReach(BlockPos pos) {
		double range = mc.player.blockInteractionRange();
		return Vec3.atCenterOf(pos).distanceToSqr(mc.player.getEyePosition()) <= range * range;
	}
}
