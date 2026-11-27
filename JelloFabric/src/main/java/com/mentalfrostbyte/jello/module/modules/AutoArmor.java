package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.Equippable;

/**
 * Moves the best armor from the inventory into the armor slots while the inventory is open.
 * The 1.8 version hard-coded numeric item ids; 26.x reads the equippable slot and armor attribute.
 */
public class AutoArmor extends Module {
	private final TimerUtil timer = new TimerUtil();
	private final NumberValue delay = addValue(new NumberValue("Delay", 40, 1, 500, 1));
	public boolean bestarmor = true;

	public AutoArmor() {
		super("AutoArmor", 0, Category.ITEM);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || !(mc.gui.screen() instanceof net.minecraft.client.gui.screens.inventory.InventoryScreen)) {
			return;
		}
		Player player = mc.player;
		if (!(player.containerMenu instanceof InventoryMenu menu) || !timer.hasTimeElapsed((long) delay.getValue(), true)) {
			return;
		}
		List<net.minecraft.world.inventory.Slot> slots = menu.slots;
		for (EquipmentSlot equipment : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
			ItemStack worn = player.getItemBySlot(equipment);
			boolean empty = worn.isEmpty();
			double current = armorValue(worn);
			int bestSlot = -1;
			double best = bestarmor ? current : -1;
			for (int i = 9; i < 45 && i < slots.size(); i++) {
				ItemStack stack = slots.get(i).getItem();
				if (equipmentOf(stack) != equipment) {
					continue;
				}
				double value = armorValue(stack);
				// "Best armor" upgrades worn pieces; otherwise only empty slots get filled.
				if (empty ? value > best : bestarmor && value > best) {
					best = value;
					bestSlot = i;
				}
			}
			if (bestSlot != -1) {
				mc.gameMode.handleContainerInput(menu.containerId, bestSlot, 0, ContainerInput.QUICK_MOVE, player);
				return;
			}
		}
	}

	private static EquipmentSlot equipmentOf(ItemStack stack) {
		Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
		return equippable == null || !equippable.slot().isArmor() ? null : equippable.slot();
	}

	private static double armorValue(ItemStack stack) {
		EquipmentSlot slot = equipmentOf(stack);
		if (slot == null) {
			return -1;
		}
		double armor = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)
			.compute(Attributes.ARMOR, 0, slot);
		int protection = 0;
		ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		for (Holder<Enchantment> holder : enchantments.keySet()) {
			if (holder.is(Enchantments.PROTECTION)) {
				protection = enchantments.getLevel(holder);
			}
		}
		return armor + (100 - armor * 4) * protection * 4 * 0.0075;
	}
}
