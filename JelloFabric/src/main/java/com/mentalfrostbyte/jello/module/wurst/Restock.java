package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

/**
 * Wurst Restock: refills the selected hotbar/offhand stack from the rest of the inventory.
 *
 * <p>Wurst has an ItemListSetting for the items to restock; Jello only has Boolean/Number/Mode
 * settings, so this version restocks whatever item currently sits in the target slot.</p>
 */
public class Restock extends Module {
	private final NumberValue slot = addValue(new NumberValue("Slot", 0, -1, 9, 1));
	private final NumberValue minimum = addValue(new NumberValue("Minimum Amount", 1, 1, 64, 1));
	private final NumberValue repair = addValue(new NumberValue("Tools Repair Mode", 0, 0, 100, 1));

	public Restock() {
		super("Restock", 0, Category.ITEM);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.gameMode == null || mc.gui.screen() != null) {
			return;
		}
		int target = (int) slot.getValue();
		if (target == -1) {
			target = mc.player.getInventory().getSelectedSlot();
		} else if (target == 9) {
			target = 40;
		}
		ItemStack stack = mc.player.getInventory().getItem(target);
		if (stack.isEmpty()) {
			return;
		}
		if (repair.getValue() > 0 && stack.isDamageableItem()
			&& stack.getMaxDamage() - stack.getDamageValue() <= repair.getValue()) {
			moveToEmptySlot(target);
			return;
		}
		if (stack.getCount() >= Math.min(minimum.getValue(), stack.getMaxStackSize())) {
			return;
		}
		for (int i = 0; i < 41; i++) {
			if (i == target) {
				continue;
			}
			ItemStack other = mc.player.getInventory().getItem(i);
			if (other.isEmpty() || !other.is(stack.getItem())) {
				continue;
			}
			pickup(i);
			pickup(target);
			if (!mc.player.inventoryMenu.getCarried().isEmpty()) {
				pickup(i);
			}
			if (mc.player.getInventory().getItem(target).getCount() >= stack.getMaxStackSize()) {
				return;
			}
		}
	}

	private void moveToEmptySlot(int from) {
		for (int i = 9; i < 36; i++) {
			if (i == from || !mc.player.getInventory().getItem(i).isEmpty()) {
				continue;
			}
			pickup(from);
			pickup(i);
			return;
		}
	}

	private void pickup(int inventorySlot) {
		mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, toNetworkSlot(inventorySlot),
			0, ContainerInput.PICKUP, mc.player);
	}

	private static int toNetworkSlot(int slot) {
		if (slot >= 0 && slot < 9) {
			return 36 + slot;
		}
		if (slot == 40) {
			return 45;
		}
		return slot;
	}
}
