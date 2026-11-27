package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

/** Reborn InvManager (inventory cleanup) built on the 26.3 container API. */
public class InvManager extends Module {
	private final NumberValue delay = addValue(new NumberValue("Delay", 200, 1, 1000, 1));
	private final NumberValue startDelay = addValue(new NumberValue("StartDelay", 1, 1, 1000, 1));
	private final BooleanValue openInv = addValue(new BooleanValue("InvOpen", true));
	private final BooleanValue noMove = addValue(new BooleanValue("NoMove", true));
	private final TimerUtil timer = new TimerUtil();
	private int slot = 9;
	private boolean waiting;

	public InvManager() {
		super("InvManager", com.mojang.blaze3d.platform.InputConstants.KEY_I, Category.ITEM);
	}

	@Override
	public void onEnable() {
		slot = 9;
		waiting = false;
		timer.reset();
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null) {
			return;
		}
		if (openInv.getValue() && !(mc.gui.screen() instanceof InventoryScreen)) {
			return;
		}
		if (noMove.getValue() && !p.onGround()) {
			return;
		}
		if (waiting) {
			if (timer.hasTimeElapsed((long) delay.getValue(), false)) {
				waiting = false;
				timer.reset();
			}
			return;
		}
		if (slot >= 45) {
			setToggled(false);
			return;
		}
		var menu = p.containerMenu;
		if (slot >= menu.slots.size()) {
			slot = 45;
			return;
		}
		ItemStack stack = menu.slots.get(slot).getItem();
		if (InvCleaner.isItemBad(stack) && stack != p.getMainHandItem()) {
			mc.gameMode.handleContainerInput(menu.containerId, slot, 1, ContainerInput.THROW, p);
			waiting = true;
			timer.reset();
		}
		slot++;
	}
}
