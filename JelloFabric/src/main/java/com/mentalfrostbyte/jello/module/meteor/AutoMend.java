package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/** Meteor Client auto-mend: puts damaged mending gear in the offhand until it is repaired. */
public class AutoMend extends Module {
	public AutoMend() {
		super("Auto Mend", 0, Category.PLAYER);
	}

	private final BooleanValue force = addValue(new BooleanValue("Force", false));
	private final BooleanValue autoDisable = addValue(new BooleanValue("Auto Disable", true));

	private boolean didMove;

	@Override
	public void onEnable() {
		didMove = false;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || !(mc.player.containerMenu instanceof InventoryMenu menu)) {
			return;
		}
		if (shouldWait()) {
			return;
		}

		int slot = findMendingItem();
		if (slot == -1) {
			if (autoDisable.getValue()) {
				if (didMove) {
					int empty = findEmptySlot();
					if (empty != -1) {
						move(menu, inventorySlotId(empty), 45);
					}
				}
				setToggled(false);
			}
			return;
		}
		move(menu, inventorySlotId(slot), 45);
		didMove = true;
	}

	private boolean shouldWait() {
		ItemStack offhand = mc.player.getOffhandItem();
		if (offhand.isEmpty()) {
			return false;
		}
		if (hasMending(offhand)) {
			return offhand.getDamageValue() != 0;
		}
		return !force.getValue();
	}

	private int findMendingItem() {
		for (int i = 0; i < mc.player.getInventory().getNonEquipmentItems().size(); i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (hasMending(stack) && stack.getDamageValue() > 0) {
				return i;
			}
		}
		return -1;
	}

	private int findEmptySlot() {
		for (int i = 0; i < mc.player.getInventory().getNonEquipmentItems().size(); i++) {
			if (mc.player.getInventory().getItem(i).isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	/** Two pick-up clicks: grab the source, then drop it into the destination (Meteor's InvUtils.move). */
	private void move(InventoryMenu menu, int from, int to) {
		mc.gameMode.handleContainerInput(menu.containerId, from, 0, ContainerInput.PICKUP, mc.player);
		mc.gameMode.handleContainerInput(menu.containerId, to, 0, ContainerInput.PICKUP, mc.player);
		// Put back whatever the destination swapped into the cursor.
		if (!mc.player.containerMenu.getCarried().isEmpty()) {
			mc.gameMode.handleContainerInput(menu.containerId, from, 0, ContainerInput.PICKUP, mc.player);
		}
	}

	/** Inventory index (0-8 hotbar, 9-35 main, 36-39 armor, 40 offhand) to InventoryMenu slot id. */
	private static int inventorySlotId(int index) {
		if (index < 9) {
			return 36 + index;
		}
		if (index < 36) {
			return index;
		}
		if (index < 40) {
			return 5 + index - 36;
		}
		return 45;
	}

	private static boolean hasMending(ItemStack stack) {
		ItemEnchantments enchantments = stack.getEnchantments();
		for (Holder<Enchantment> holder : enchantments.keySet()) {
			if (holder.is(Enchantments.MENDING)) {
				return true;
			}
		}
		return false;
	}
}
