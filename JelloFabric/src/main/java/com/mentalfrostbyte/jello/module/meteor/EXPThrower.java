package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Meteor Client exp-thrower: throws experience bottles from the hotbar. */
public class EXPThrower extends Module {
	public EXPThrower() {
		super("Exp Thrower", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.gui.screen() != null) {
			return;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (stack.is(Items.EXPERIENCE_BOTTLE)) {
				mc.player.getInventory().setSelectedSlot(i);
				mc.player.setXRot(90.0F);
				mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
				return;
			}
		}
	}
}
