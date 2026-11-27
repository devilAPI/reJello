package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

/** Meteor Client auto-replenish: refills hotbar and offhand stacks from the inventory. */
public class AutoReplenish extends Module {
	public AutoReplenish() {
		super("Auto Replenish", 0, Category.PLAYER);
	}

	private final NumberValue minCount = addValue(new NumberValue("Min Count", 8, 1, 63, 1));
	private final NumberValue delay = addValue(new NumberValue("Delay", 1, 0, 100, 1));
	private final BooleanValue offhand = addValue(new BooleanValue("Offhand", true));
	private final BooleanValue unstackable = addValue(new BooleanValue("Unstackable", true));
	private final BooleanValue sameEnchants = addValue(new BooleanValue("Same Enchants", true));
	private final BooleanValue searchHotbar = addValue(new BooleanValue("Search Hotbar", false));

	private final ItemStack[] items = new ItemStack[10];
	private boolean prevHadOpenScreen;
	private int tickDelayLeft;

	@Override
	public void onEnable() {
		fillItems();
		tickDelayLeft = (int) delay.getValue();
		prevHadOpenScreen = mc.gui.screen() != null;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		if (mc.gui.screen() == null && prevHadOpenScreen) {
			fillItems();
		}
		prevHadOpenScreen = mc.gui.screen() != null;

		if (!(mc.player.containerMenu instanceof InventoryMenu menu) || mc.gui.screen() != null) {
			return;
		}
		if (tickDelayLeft > 0) {
			tickDelayLeft--;
			return;
		}

		for (int i = 0; i < 9; i++) {
			checkSlot(menu, i, mc.player.getInventory().getItem(i));
		}
		if (offhand.getValue()) {
			checkSlot(menu, 9, mc.player.getOffhandItem());
		}
		tickDelayLeft = (int) delay.getValue();
	}

	private void checkSlot(InventoryMenu menu, int slot, ItemStack stack) {
		ItemStack prevStack = items[slot];
		items[slot] = stack.copy();
		if (prevStack == null) {
			prevStack = ItemStack.EMPTY;
		}

		if (slot == 9 && !offhand.getValue()) {
			return;
		}

		int destinationId = slot == 9 ? 45 : 36 + slot;
		int fromSlot = -1;

		if (stack.isStackable() && !stack.isEmpty() && stack.getCount() <= minCount.getValue()) {
			fromSlot = findItem(stack, slot == 9 ? -1 : slot, (int) minCount.getValue() - stack.getCount() + 1, true);
		}
		if (prevStack.isStackable() && stack.isEmpty() && !prevStack.isEmpty()) {
			fromSlot = findItem(prevStack, slot == 9 ? -1 : slot, (int) minCount.getValue() - stack.getCount() + 1, false);
		}
		if (unstackable.getValue() && !prevStack.isStackable() && stack.isEmpty() && !prevStack.isEmpty()) {
			fromSlot = findItem(prevStack, slot == 9 ? -1 : slot, 1, false);
		}

		if (fromSlot == -1 || fromSlot == mc.player.getInventory().getSelectedSlot()) {
			return;
		}
		if (fromSlot < 9 && fromSlot < slot && slot != mc.player.getInventory().getSelectedSlot() && slot != 9) {
			return;
		}

		int sourceId = inventorySlotId(fromSlot);
		mc.gameMode.handleContainerInput(menu.containerId, sourceId, 0, ContainerInput.PICKUP, mc.player);
		mc.gameMode.handleContainerInput(menu.containerId, destinationId, 0, ContainerInput.PICKUP, mc.player);
		// Put back any remainder the destination could not absorb.
		if (!mc.player.containerMenu.getCarried().isEmpty()) {
			mc.gameMode.handleContainerInput(menu.containerId, sourceId, 0, ContainerInput.PICKUP, mc.player);
		}
	}

	private int findItem(ItemStack lookFor, int excludedSlot, int goodEnoughCount, boolean mustCombine) {
		int slot = -1;
		int count = 0;
		int min = searchHotbar.getValue() ? 0 : 9;

		for (int i = mc.player.getInventory().getNonEquipmentItems().size() - 1; i >= min; i--) {
			if (i == excludedSlot) {
				continue;
			}
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (stack.getItem() != lookFor.getItem()) {
				continue;
			}
			if (mustCombine && !ItemStack.isSameItemSameComponents(lookFor, stack)) {
				continue;
			}
			if (sameEnchants.getValue() && !stack.getEnchantments().equals(lookFor.getEnchantments())) {
				continue;
			}
			if (stack.getCount() > count) {
				slot = i;
				count = stack.getCount();
				if (count >= goodEnoughCount) {
					break;
				}
			}
		}
		return slot;
	}

	private void fillItems() {
		if (mc.player == null) {
			return;
		}
		for (int i = 0; i < 9; i++) {
			items[i] = mc.player.getInventory().getItem(i).copy();
		}
		items[9] = mc.player.getOffhandItem().copy();
	}

	private static int inventorySlotId(int index) {
		return index < 9 ? 36 + index : index;
	}
}
