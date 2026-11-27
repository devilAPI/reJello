package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Meteor Client bow-spam: fires bows at minimum charge and empties loaded crossbows. */
public class BowSpam extends Module {
	private final NumberValue charge = addValue(new NumberValue("Charge", 5, 4, 20, 1));
	private final BooleanValue onlyWhenHoldingRightClick = addValue(new BooleanValue("When Holding Right Click", false));
	private final BooleanValue spamCrossbows = addValue(new BooleanValue("Spam Crossbows", true));
	private final NumberValue crossbowDelay = addValue(new NumberValue("Crossbow Delay", 10, 0, 20, 1));
	private final BooleanValue searchInventory = addValue(new BooleanValue("Search Inventory", true));

	private boolean wasBow;
	private boolean wasHoldingRightClick;
	private int ticks;
	private int restoreSlot = -1;
	private int restoreHotbar = -1;
	private int restoreTicks;

	public BowSpam() {
		super("Bow Spam", 0, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		wasBow = false;
		wasHoldingRightClick = false;
		ticks = 0;
		restoreSlot = -1;
		restoreHotbar = -1;
		restoreTicks = 0;
	}

	@Override
	public void onDisable() {
		if (mc.options != null) {
			mc.options.keyUse.setDown(false);
		}
		restoreSwap();
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (restoreTicks > 0 && --restoreTicks == 0) {
			restoreSwap();
		}

		if (spamCrossbows.getValue()) {
			int crossbow = findChargedCrossbowSlot();
			if (crossbow != -1) {
				if (ticks++ >= (int) crossbowDelay.getValue()) {
					ticks = 0;
					useCrossbow(crossbow);
				}
				return;
			}
		}

		if (!mc.player.getAbilities().instabuild && !hasArrow()) {
			release();
			return;
		}

		if (onlyWhenHoldingRightClick.getValue() && !mc.options.keyUse.isDown()) {
			if (wasHoldingRightClick) {
				release();
				wasHoldingRightClick = false;
			}
			return;
		}

		boolean isBow = mc.player.getMainHandItem().is(Items.BOW);
		if (!isBow) {
			if (wasBow) {
				release();
			}
			wasBow = false;
			return;
		}
		wasBow = true;

		if (mc.player.getTicksUsingItem() >= (int) charge.getValue()) {
			mc.gameMode.releaseUsingItem(mc.player);
		} else {
			mc.options.keyUse.setDown(true);
		}
		wasHoldingRightClick = mc.options.keyUse.isDown();
	}

	private void useCrossbow(int slot) {
		if (mc.player == null || mc.player.containerMenu == null || mc.gameMode == null) {
			return;
		}
		int hotbarSlot = mc.player.getInventory().getSelectedSlot();
		if (slot >= 9) {
			mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, slot, hotbarSlot,
				ContainerInput.SWAP, mc.player);
			restoreSlot = slot;
			restoreHotbar = hotbarSlot;
			restoreTicks = Math.max(2, (int) crossbowDelay.getValue());
		}
		MeteorCombatHelper.selectSlot(hotbarSlot);
		mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
	}

	private int findChargedCrossbowSlot() {
		if (mc.player == null) {
			return -1;
		}
		for (int i = 0; i < 9; i++) {
			if (CrossbowItem.isCharged(mc.player.getInventory().getItem(i))) {
				return i;
			}
		}
		if (searchInventory.getValue()) {
			for (int i = 9; i < 36; i++) {
				if (CrossbowItem.isCharged(mc.player.getInventory().getItem(i))) {
					return i;
				}
			}
		}
		return -1;
	}

	private boolean hasArrow() {
		if (mc.player == null) {
			return false;
		}
		for (int i = 0; i < 36; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (!stack.isEmpty() && stack.is(Items.ARROW)) {
				return true;
			}
		}
		return mc.player.getInventory().getItem(40).is(Items.ARROW);
	}

	private void restoreSwap() {
		if (restoreSlot != -1 && restoreHotbar != -1 && mc.player != null
			&& mc.player.containerMenu != null && mc.gameMode != null) {
			mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, restoreSlot, restoreHotbar,
				ContainerInput.SWAP, mc.player);
		}
		restoreSlot = -1;
		restoreHotbar = -1;
	}

	private void release() {
		if (mc.options != null) {
			mc.options.keyUse.setDown(false);
		}
	}
}
