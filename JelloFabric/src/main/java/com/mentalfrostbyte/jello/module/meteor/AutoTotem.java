package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

/** Meteor Client auto-totem: keeps a totem in the offhand. */
public class AutoTotem extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Smart", "Smart", "Strict"));
	private final NumberValue delay = addValue(new NumberValue("Delay", 0, 0, 500, 5));
	private final NumberValue health = addValue(new NumberValue("Health", 10, 1, 20, 1));
	private final BooleanValue elytra = addValue(new BooleanValue("Elytra", true));
	private final BooleanValue fall = addValue(new BooleanValue("Fall", true));
	private final BooleanValue explosion = addValue(new BooleanValue("Explosion", true));
	private final TimerUtil timer = new TimerUtil();

	public AutoTotem() {
		super("Auto Totem", 0, Category.COMBAT);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.player.containerMenu == null) {
			return;
		}
		if (isTotem(mc.player.getOffhandItem()) || !timer.hasTimeElapsed((long) delay.getValue(), true)) {
			return;
		}
		boolean needs = mode.is("Strict")
			|| mc.player.getHealth() <= health.getValue()
			|| (fall.getValue() && mc.player.fallDistance > 3)
			|| (explosion.getValue() && mc.player.hurtTime > 0)
			|| (elytra.getValue() && mc.player.isFallFlying());
		if (!needs) {
			return;
		}
		var slots = mc.player.containerMenu.slots;
		for (int i = 9; i < slots.size(); i++) {
			ItemStack stack = slots.get(i).getItem();
			if (isTotem(stack)) {
				mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, i, 40, ContainerInput.SWAP, mc.player);
				return;
			}
		}
	}

	private static boolean isTotem(ItemStack stack) {
		return !stack.isEmpty() && net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("totem");
	}
}
