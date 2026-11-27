package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Meteor Client chest-swap: toggles between a chestplate and an elytra. */
public class ChestSwap extends Module {
	private final ModeValue chestplate = addValue(new ModeValue("Chestplate", "Prefer Netherite",
		"Prefer Netherite", "Netherite", "Diamond", "Prefer Diamond"));
	private final BooleanValue stayOn = addValue(new BooleanValue("Stay On", false));
	private final BooleanValue closeInventory = addValue(new BooleanValue("Close Inventory", true));

	public ChestSwap() {
		super("Chest Swap", 0, Category.PLAYER);
	}

	@Override
	public void onEnable() {
		swap();
		if (!stayOn.getValue()) {
			setToggled(false);
		}
	}

	@Override
	public void onDisable() {
		if (stayOn.getValue()) {
			swap();
		}
	}

	private void swap() {
		if (mc.player == null || !(mc.player.containerMenu instanceof InventoryMenu menu)) {
			return;
		}
		ItemStack current = mc.player.getItemBySlot(EquipmentSlot.CHEST);
		if (current.has(DataComponents.GLIDER)) {
			equipChestplate(menu);
		} else if (current.has(DataComponents.EQUIPPABLE)
			&& current.get(DataComponents.EQUIPPABLE).slot() == EquipmentSlot.CHEST) {
			equipElytra(menu);
		} else if (!equipChestplate(menu)) {
			equipElytra(menu);
		}
	}

	private boolean equipChestplate(InventoryMenu menu) {
		int bestSlot = -1;
		boolean done = false;
		int size = mc.player.getInventory().getNonEquipmentItems().size();

		for (int i = 0; i < size && !done; i++) {
			Item item = mc.player.getInventory().getItem(i).getItem();
			switch (chestplate.getMode()) {
				case "Diamond" -> {
					if (item == Items.DIAMOND_CHESTPLATE) {
						bestSlot = i;
						done = true;
					}
				}
				case "Netherite" -> {
					if (item == Items.NETHERITE_CHESTPLATE) {
						bestSlot = i;
						done = true;
					}
				}
				case "Prefer Diamond" -> {
					if (item == Items.DIAMOND_CHESTPLATE) {
						bestSlot = i;
						done = true;
					} else if (item == Items.NETHERITE_CHESTPLATE) {
						bestSlot = i;
					}
				}
				default -> {
					if (item == Items.NETHERITE_CHESTPLATE) {
						bestSlot = i;
						done = true;
					} else if (item == Items.DIAMOND_CHESTPLATE) {
						bestSlot = i;
					}
				}
			}
		}

		if (bestSlot != -1) {
			equip(menu, bestSlot);
		}
		return bestSlot != -1;
	}

	private void equipElytra(InventoryMenu menu) {
		int size = mc.player.getInventory().getNonEquipmentItems().size();
		for (int i = 0; i < size; i++) {
			if (mc.player.getInventory().getItem(i).has(DataComponents.GLIDER)) {
				equip(menu, i);
				return;
			}
		}
	}

	/** Pick up the worn chest item, swap it with the chosen one, then put the new one on. */
	private void equip(InventoryMenu menu, int inventoryIndex) {
		int slot = inventorySlotId(inventoryIndex);
		mc.gameMode.handleContainerInput(menu.containerId, 6, 0, ContainerInput.PICKUP, mc.player);
		mc.gameMode.handleContainerInput(menu.containerId, slot, 0, ContainerInput.PICKUP, mc.player);
		mc.gameMode.handleContainerInput(menu.containerId, 6, 0, ContainerInput.PICKUP, mc.player);

		if (closeInventory.getValue() && mc.getConnection() != null) {
			mc.getConnection().send(new ServerboundContainerClosePacket(0));
		}
	}

	private static int inventorySlotId(int index) {
		return index < 9 ? 36 + index : index;
	}
}
