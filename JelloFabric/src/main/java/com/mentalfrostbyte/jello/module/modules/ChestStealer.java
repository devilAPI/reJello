package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;

/** Shift-clicks random chest slots into the inventory, like the original. */
public class ChestStealer extends Module {
	private final Random random = new Random();
	private final List<Integer> chestSlots = new ArrayList<>();

	public ChestStealer() {
		super("Chest Stealer", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || !(mc.gui.screen() instanceof AbstractContainerScreen<?> screen)) {
			return;
		}
		ChestMenu menu = screen.getMenu() instanceof ChestMenu chest ? chest : null;
		if (menu == null) {
			return;
		}
		if (mc.player.tickCount % 4 != 0) {
			return;
		}
		chestSlots.clear();
		int chestSize = menu.slots.size() - 36;
		for (int i = 0; i < chestSize; i++) {
			Slot slot = menu.slots.get(i);
			if (slot.hasItem()) {
				chestSlots.add(i);
			}
		}
		if (chestSlots.isEmpty()) {
			// Nothing left: close the screen and restore the pressed movement keys.
			mc.player.closeContainer();
			return;
		}
		int slot = chestSlots.get(random.nextInt(chestSlots.size()));
		mc.gameMode.handleContainerInput(menu.containerId, slot, 0, ContainerInput.QUICK_MOVE, mc.player);
	}
}
