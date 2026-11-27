package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventAttack;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.core.component.DataComponents;

/** Meteor Client auto-weapon: selects the strongest hotbar weapon before an attack. */
public class AutoWeapon extends Module {
	private final BooleanValue onlyClick = addValue(new BooleanValue("Only On Click", true));

	public AutoWeapon() {
		super("Auto Weapon", 0, Category.COMBAT);
	}

	@EventTarget
	public void onAttack(EventAttack event) {
		if (!isToggled() || mc.player == null || mc.player.getInventory() == null) {
			return;
		}
		int best = -1;
		double bestDamage = -1;
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (stack.isEmpty() || stack.get(DataComponents.WEAPON) == null) {
				continue;
			}
			double damage = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)
				.compute(Attributes.ATTACK_DAMAGE, 0, EquipmentSlot.MAINHAND);
			if (damage > bestDamage) {
				bestDamage = damage;
				best = i;
			}
		}
		if (best != -1) {
			mc.player.getInventory().setSelectedSlot(best);
		}
	}
}
