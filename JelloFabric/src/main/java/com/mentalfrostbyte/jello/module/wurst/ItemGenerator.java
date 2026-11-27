package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Wurst ItemGenerator: fills inventory slots with random items and drops them (creative only). */
public class ItemGenerator extends Module {
	private final NumberValue speed = addValue(new NumberValue("Speed", 3, 1, 9, 1));
	private final NumberValue stackSize = addValue(new NumberValue("Stack Size", 1, 1, 64, 1));

	private final RandomSource random = RandomSource.create();

	public ItemGenerator() {
		super("Item Generator", 0, Category.ITEM);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.gameMode == null) {
			return;
		}
		if (!mc.player.getAbilities().instabuild) {
			Jello.addChatMessage("Creative mode only.");
			setToggled(false);
			return;
		}
		int stacks = (int) speed.getValue();
		for (int slot = 9; slot < 9 + stacks && slot < 36; slot++) {
			Holder.Reference<Item> holder = null;
			while (holder == null) {
				holder = BuiltInRegistries.ITEM.getRandom(random).orElse(null);
			}
			Item item = holder.value();
			if (item == Items.AIR) {
				continue;
			}
			mc.gameMode.handleCreativeModeItemAdd(new ItemStack(item, (int) stackSize.getValue()), slot);
		}
		for (int slot = 9; slot < 9 + stacks && slot < 36; slot++) {
			mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, slot, 1,
				ContainerInput.THROW, mc.player);
		}
	}
}
