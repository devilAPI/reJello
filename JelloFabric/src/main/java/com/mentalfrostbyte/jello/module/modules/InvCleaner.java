package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.util.TimerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.equipment.Equippable;

/** Throws away the item classes the 1.8 InvCleaner considered junk. */
public class InvCleaner extends Module {
	private final TimerUtil timer = new TimerUtil();
	private int slots = 9;
	private boolean waiting;

	public InvCleaner() {
		super("InvCleaner", 0, Category.ITEM);
	}

	@Override
	public void onEnable() {
		slots = 9;
		waiting = false;
		timer.reset();
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || !(mc.gui.screen() instanceof InventoryScreen)) {
			return;
		}
		Player player = mc.player;
		if (!(player.containerMenu instanceof InventoryMenu menu)) {
			return;
		}
		List<Slot> all = menu.slots;
		if (waiting) {
			if (timer.delay(100)) {
				waiting = false;
				timer.reset();
			}
			return;
		}
		if (slots >= 45) {
			setToggled(false);
			return;
		}
		if (slots >= all.size()) {
			slots = 45;
			return;
		}
		ItemStack stack = all.get(slots).getItem();
		if (isItemBad(stack) && stack != player.getMainHandItem()) {
			mc.gameMode.handleContainerInput(menu.containerId, slots, 1, ContainerInput.THROW, player);
			waiting = true;
			timer.reset();
		}
		slots++;
	}

	public static boolean isItemBad(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
		if (path.contains("tnt") || path.contains("stick") || path.contains("egg") || path.contains("string")
			|| path.contains("flint") || path.contains("compass") || path.contains("feather") || path.contains("bucket")
			|| path.contains("chest") || path.contains("snowball") || path.contains("fish") || path.contains("enchant")
			|| path.contains("exp")) {
			return true;
		}
		if (stack.get(DataComponents.TOOL) != null || isArmor(stack) || stack.get(DataComponents.WEAPON) != null) {
			return true;
		}
		return stack.get(DataComponents.POTION_CONTENTS) != null && isBadPotion(stack);
	}

	public static boolean isBadPotion(ItemStack stack) {
		PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
		if (contents == null) {
			return false;
		}
		for (MobEffectInstance effect : contents.getAllEffects()) {
			if (effect.getEffect().is(MobEffects.POISON) || effect.getEffect().is(MobEffects.SLOWNESS)
				|| effect.getEffect().is(MobEffects.INSTANT_DAMAGE)) {
				return true;
			}
		}
		return false;
	}

	static boolean isArmor(ItemStack stack) {
		Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
		return equippable != null && equippable.slot().isArmor();
	}
}
