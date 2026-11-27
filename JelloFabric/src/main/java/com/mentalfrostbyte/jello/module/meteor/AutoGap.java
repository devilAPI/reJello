package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/** Meteor Client auto-gap: eats golden apples when needed. */
public class AutoGap extends Module {
	private final NumberValue health = addValue(new NumberValue("Health Threshold", 20, 1, 20, 1));
	private final BooleanValue allowEgap = addValue(new BooleanValue("Allow Egap", true));
	private final BooleanValue always = addValue(new BooleanValue("Always", false));

	public AutoGap() {
		super("Auto Gap", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.gui.screen() != null || mc.player.isUsingItem()) {
			return;
		}
		if (!always.getValue() && mc.player.getHealth() > health.getValue()) {
			return;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (isGap(stack)) {
				mc.player.getInventory().setSelectedSlot(i);
				mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
				return;
			}
		}
	}

	private boolean isGap(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
		return path.equals("golden_apple") || (allowEgap.getValue() && path.equals("enchanted_golden_apple"));
	}
}
