package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;

/** Wurst AutoSoup: eats mushroom/rabbit stew or beetroot soup when health gets low. */
public class AutoSoup extends Module {
	private final NumberValue health = addValue(new NumberValue("Health", 13, 1, 19, 1));

	private int oldSlot = -1;

	public AutoSoup() {
		super("Auto Soup", 0, Category.COMBAT);
	}

	@Override
	public void onDisable() {
		stopEating();
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gui.screen() != null) {
			return;
		}
		if (mc.player.getHealth() > health.getValue()) {
			stopEating();
			return;
		}
		if (isClickable()) {
			return;
		}
		for (int i = 0; i < 9; i++) {
			if (!isSoup(mc.player.getInventory().getItem(i))) {
				continue;
			}
			if (oldSlot == -1) {
				oldSlot = mc.player.getInventory().getSelectedSlot();
			}
			mc.player.getInventory().setSelectedSlot(i);
			mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
			return;
		}
	}

	private boolean isClickable() {
		if (mc.hitResult instanceof BlockHitResult hit) {
			return mc.level.getBlockState(hit.getBlockPos()).hasBlockEntity();
		}
		return mc.hitResult instanceof net.minecraft.world.phys.EntityHitResult;
	}

	private static boolean isSoup(ItemStack stack) {
		return !stack.isEmpty() && (stack.is(Items.MUSHROOM_STEW) || stack.is(Items.RABBIT_STEW)
			|| stack.is(Items.BEETROOT_SOUP));
	}

	private void stopEating() {
		if (oldSlot != -1 && mc.player != null) {
			mc.player.getInventory().setSelectedSlot(oldSlot);
		}
		oldSlot = -1;
	}
}
