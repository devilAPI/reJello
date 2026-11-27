package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import java.util.Set;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

/**
 * Meteor Client inventory tweaks. Ported subset: XCarry (cancels the inventory-menu close packet), auto steal/dump
 * via shift-clicking while a container screen is open, and junk auto-drop. The sorter, item-list anti-drop and
 * drag handling need container-screen accessors that this build does not have.
 */
public class InventoryTweaks extends Module {
	private static final Set<String> JUNK = Set.of(
		"rotten_flesh", "poisonous_potato", "spider_eye", "pufferfish", "tropical_fish", "bone", "string",
		"gunpowder", "egg", "feather", "flint", "leather", "wheat_seeds", "beetroot_seeds", "melon_seeds",
		"pumpkin_seeds", "torchflower_seeds", "pitcher_pod"
	);

	private final BooleanValue xcarry = addValue(new BooleanValue("XCarry", true));
	private final BooleanValue autoSteal = addValue(new BooleanValue("Auto Steal", false));
	private final BooleanValue autoDump = addValue(new BooleanValue("Auto Dump", false));
	private final NumberValue delay = addValue(new NumberValue("Delay", 100, 0, 1000, 10));
	private final ModeValue autoDrop = addValue(new ModeValue("Auto Drop", "None", "None", "Junk"));
	private final BooleanValue excludeHotbar = addValue(new BooleanValue("Exclude Hotbar", false));
	private final BooleanValue onlyFullStacks = addValue(new BooleanValue("Only Full Stacks", false));

	private final TimerUtil timer = new TimerUtil();
	private boolean invOpened;

	public InventoryTweaks() {
		super("Inventory Tweaks", 0, Category.MISC);
	}

	@Override
	public void onEnable() {
		invOpened = false;
		timer.reset();
	}

	@Override
	public void onDisable() {
		if (invOpened && mc.player != null && mc.getConnection() != null) {
			mc.getConnection().send(new ServerboundContainerClosePacket(mc.player.inventoryMenu.containerId));
		}
		invOpened = false;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		autoDropItems();
		moveSlots();
	}

	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (!isToggled() || !xcarry.getValue() || mc.player == null) {
			return;
		}
		if (event.getPacket() instanceof ServerboundContainerClosePacket packet
			&& packet.getContainerId() == mc.player.inventoryMenu.containerId) {
			invOpened = true;
			event.setCancelled(true);
		}
	}

	private void autoDropItems() {
		if (autoDrop.is("None") || mc.gui.screen() instanceof AbstractContainerScreen<?>) {
			return;
		}
		int start = excludeHotbar.getValue() ? 9 : 0;
		for (int i = start; i < 36; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (stack.isEmpty() || !JUNK.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath())) {
				continue;
			}
			if (onlyFullStacks.getValue() && stack.count() < stack.getMaxStackSize()) {
				continue;
			}
			int menuSlot = i < 9 ? i + 36 : i;
			mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, menuSlot, 1, ContainerInput.THROW, mc.player);
			return;
		}
	}

	private void moveSlots() {
		if (!(mc.gui.screen() instanceof AbstractContainerScreen<?> screen)) {
			return;
		}
		var menu = screen.getMenu();
		if (menu == mc.player.inventoryMenu || (!autoSteal.getValue() && !autoDump.getValue())
			|| !timer.hasTimeElapsed((long) delay.getValue(), true)) {
			return;
		}
		int size = menu.slots.size();
		if (size <= 36) {
			return;
		}
		int containerSlots = size - 36;
		int from = autoDump.getValue() ? containerSlots : 0;
		int to = autoDump.getValue() ? containerSlots + 27 : containerSlots;
		for (int i = from; i < to; i++) {
			if (menu.slots.get(i).hasItem()) {
				mc.gameMode.handleContainerInput(menu.containerId, i, 0, ContainerInput.QUICK_MOVE, mc.player);
				return;
			}
		}
	}
}
