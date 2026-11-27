package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventKeyPressed;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/** Meteor Client middle-click-extra: middle click uses a pearl, rod or gap. */
public class MiddleClickExtra extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Pearl", "Pearl", "Rod", "Gap"));

	public MiddleClickExtra() {
		super("Middle Click Extra", 0, Category.PLAYER);
	}

	@EventTarget
	public void onKey(EventKeyPressed event) {
		if (!isToggled() || mc.player == null || mc.gui.screen() != null) {
			return;
		}
		InputConstants.Key key = event.getKey();
		if (key.getType() != InputConstants.Type.MOUSE || key.getValue() != 2) {
			return;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (matches(stack)) {
				mc.player.getInventory().setSelectedSlot(i);
				mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
				return;
			}
		}
	}

	private boolean matches(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
		return switch (mode.getMode()) {
			case "Rod" -> path.contains("fishing_rod");
			case "Gap" -> path.contains("golden_apple");
			default -> path.contains("ender_pearl");
		};
	}
}
