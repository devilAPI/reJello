package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;

/** Meteor Client auto smelter: feeds fuel and smeltable items into an open furnace. */
public class AutoSmelter extends Module {
	private final NumberValue fuelPerRefill = addValue(new NumberValue("Fuel Items Per Refill", 64, 1, 64, 1));
	private final BooleanValue disableWhenOut = addValue(new BooleanValue("Disable When Out Of Items", true));
	private final BooleanValue autoClose = addValue(new BooleanValue("Auto Close", false));

	private final TimerUtil timer = new TimerUtil();

	public AutoSmelter() {
		super("Auto Smelter", 0, Category.WORLD);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		if (!(mc.player.containerMenu instanceof AbstractFurnaceMenu furnace) || mc.gui.screen() == null) {
			return;
		}
		if (!timer.hasTimeElapsed(50, true)) {
			return;
		}

		if (!checkFuel(furnace)) {
			return;
		}
		takeResults(furnace);
		insertItems(furnace);

		if (autoClose.getValue()) {
			mc.gui.setScreen(null);
		}
	}

	private boolean checkFuel(AbstractFurnaceMenu furnace) {
		if (furnace.isLit() || !furnace.slots.get(1).getItem().isEmpty()) {
			return true;
		}
		int slot = -1;
		for (int i = 3; i < furnace.slots.size(); i++) {
			ItemStack stack = furnace.slots.get(i).getItem();
			if (!stack.isEmpty() && stack.has(DataComponents.COOKING_FUEL)) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			if (disableWhenOut.getValue()) {
				setToggled(false);
				return false;
			}
			return true;
		}

		ItemStack source = furnace.slots.get(slot).getItem();
		int amount = Math.min((int) fuelPerRefill.getValue(),
			Math.min(source.getCount(), furnace.slots.get(1).getMaxStackSize(source)));
		if (amount > 0) {
			moveFuel(furnace, slot, amount);
		}
		return true;
	}

	private void moveFuel(AbstractFurnaceMenu furnace, int from, int amount) {
		if (!furnace.getCarried().isEmpty()) {
			return;
		}
		mc.gameMode.handleContainerInput(furnace.containerId, from, 0, ContainerInput.PICKUP, mc.player);
		for (int i = 0; i < amount; i++) {
			if (furnace.getCarried().isEmpty()) {
				break;
			}
			mc.gameMode.handleContainerInput(furnace.containerId, 1, 1, ContainerInput.PICKUP, mc.player);
		}
		if (!furnace.getCarried().isEmpty()) {
			mc.gameMode.handleContainerInput(furnace.containerId, from, 0, ContainerInput.PICKUP, mc.player);
		}
	}

	private void takeResults(AbstractFurnaceMenu furnace) {
		if (furnace.slots.get(2).getItem().isEmpty()) {
			return;
		}
		mc.gameMode.handleContainerInput(furnace.containerId, 2, 0, ContainerInput.QUICK_MOVE, mc.player);
		if (!furnace.slots.get(2).getItem().isEmpty()) {
			setToggled(false);
		}
	}

	private void insertItems(AbstractFurnaceMenu furnace) {
		if (!furnace.slots.get(0).getItem().isEmpty()) {
			return;
		}
		int slot = -1;
		for (int i = 3; i < furnace.slots.size(); i++) {
			ItemStack stack = furnace.slots.get(i).getItem();
			if (stack.isEmpty() || !smeltable(stack)) {
				continue;
			}
			slot = i;
			break;
		}
		if (slot == -1) {
			if (disableWhenOut.getValue()) {
				setToggled(false);
			}
			return;
		}
		mc.gameMode.handleContainerInput(furnace.containerId, slot, 0, ContainerInput.QUICK_MOVE, mc.player);
	}

	private boolean smeltable(ItemStack stack) {
		return mc.level != null
			&& mc.level.recipeAccess().propertySet(RecipePropertySet.FURNACE_INPUT).test(stack);
	}
}
