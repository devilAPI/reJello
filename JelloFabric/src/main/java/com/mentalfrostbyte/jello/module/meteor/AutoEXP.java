package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/** Meteor Client auto-exp: repairs mending gear with experience bottles. */
public class AutoEXP extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Both", "Armor", "Hands", "Both"));
	private final BooleanValue replenish = addValue(new BooleanValue("Replenish", true));
	private final BooleanValue onlyGround = addValue(new BooleanValue("Only On Ground", false));
	private final NumberValue expSlot = addValue(new NumberValue("Exp Slot", 6, 1, 9, 1));
	private final NumberValue minThreshold = addValue(new NumberValue("Min Threshold", 30, 1, 100, 1));
	private final NumberValue maxThreshold = addValue(new NumberValue("Max Threshold", 80, 1, 100, 1));

	private int repairing = -1;

	public AutoEXP() {
		super("Auto Exp", 0, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		repairing = -1;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		if (onlyGround.getValue() && !mc.player.onGround()) {
			return;
		}

		if (repairing == -1) {
			if (!mode.is("Hands")) {
				for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST,
					EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
					if (needsRepair(mc.player.getItemBySlot(slot), minThreshold.getValue())) {
						repairing = 36 + slot.getIndex();
						break;
					}
				}
			}
			if (!mode.is("Armor") && repairing == -1) {
				if (needsRepair(mc.player.getMainHandItem(), minThreshold.getValue())) {
					repairing = mc.player.getInventory().getSelectedSlot();
				} else if (needsRepair(mc.player.getOffhandItem(), minThreshold.getValue())) {
					repairing = 40;
				}
			}
		}
		if (repairing == -1) {
			return;
		}

		ItemStack target = mc.player.getInventory().getItem(repairing);
		if (!needsRepair(target, maxThreshold.getValue())) {
			repairing = -1;
			return;
		}

		int exp = MeteorCombatHelper.findItemSlot(Items.EXPERIENCE_BOTTLE, false);
		if (exp == -1) {
			return;
		}
		if (exp >= 9 && exp != 40) {
			if (!replenish.getValue()) {
				return;
			}
			int hotbarSlot = (int) expSlot.getValue() - 1;
			if (mc.gameMode == null || mc.player.containerMenu == null) {
				return;
			}
			mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, exp, hotbarSlot,
				ContainerInput.SWAP, mc.player);
			exp = hotbarSlot;
		}

		mc.player.setXRot(90.0f);
		if (exp == 40) {
			mc.gameMode.useItem(mc.player, InteractionHand.OFF_HAND);
		} else if (exp < 9) {
			MeteorCombatHelper.selectSlot(exp);
			mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
		}
	}

	private boolean needsRepair(ItemStack stack, double threshold) {
		if (stack.isEmpty() || stack.getMaxDamage() <= 0) {
			return false;
		}
		ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		boolean mending = false;
		for (Holder<Enchantment> enchantment : enchantments.keySet()) {
			if (Enchantments.MENDING.equals(enchantment.unwrapKey().orElse(null))) {
				mending = true;
				break;
			}
		}
		if (!mending) {
			return false;
		}
		double percent = (stack.getMaxDamage() - stack.getDamageValue()) * 100.0 / stack.getMaxDamage();
		return percent <= threshold;
	}
}
