package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/** Throws a healing/positive splash potion when health drops below the threshold. */
public class AutoPot extends Module {
	private final BooleanValue randomise = addValue(new BooleanValue("Randomise Rotations", true));
	private final NumberValue health = addValue(new NumberValue("Health", 15, 1, 20, 1));

	public AutoPot() {
		super("AutoPot", 0, Category.ITEM);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.gui.screen() != null || !p.onGround()) {
			return;
		}
		if (p.getHealth() > health.getValue()) {
			return;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack stack = p.getInventory().getItem(i);
			if (stack.isEmpty() || stack.get(DataComponents.POTION_CONTENTS) == null) {
				continue;
			}
			String path = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
			if (!path.contains("splash") && !path.contains("lingering")) {
				continue;
			}
			if (p.isUsingItem()) {
				return;
			}
			p.getInventory().setSelectedSlot(i);
			p.setXRot((float) (randomise.getValue() ? 89 + Math.random() * 2 : 90));
			mc.gameMode.useItem(p, InteractionHand.MAIN_HAND);
			return;
		}
	}
}
