package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/** Meteor Client auto-eat: eats food when hungry or low. */
public class AutoEat extends Module {
	private final NumberValue health = addValue(new NumberValue("Health Threshold", 10, 1, 20, 1));
	private final NumberValue hunger = addValue(new NumberValue("Hunger Threshold", 16, 1, 20, 1));
	private final BooleanValue searchInventory = addValue(new BooleanValue("Search Inventory", false));

	public AutoEat() {
		super("Auto Eat", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.gui.screen() != null || mc.player.isUsingItem()) {
			return;
		}
		if (mc.player.getHealth() > health.getValue() && mc.player.getFoodData().getFoodLevel() > hunger.getValue()) {
			return;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (isFood(stack)) {
				mc.player.getInventory().setSelectedSlot(i);
				mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
				return;
			}
		}
	}

	private static boolean isFood(ItemStack stack) {
		return !stack.isEmpty() && stack.get(DataComponents.FOOD) != null;
	}
}
