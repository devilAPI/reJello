package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Meteor Client offhand: keeps the preferred item in the offhand and falls back to a totem when in danger. */
public class Offhand extends Module {
	private final ModeValue item = addValue(new ModeValue("Item", "Crystal",
		"Crystal", "EGap", "Gap", "Totem", "Shield", "Potion"));
	private final NumberValue delay = addValue(new NumberValue("Item Switch Delay", 0, 0, 20, 1));
	private final BooleanValue hotbar = addValue(new BooleanValue("Hotbar", false));
	private final BooleanValue swordGapple = addValue(new BooleanValue("Sword Gapple", false));
	private final BooleanValue alwaysGapOnSword = addValue(new BooleanValue("Always Gap On Sword", false));
	private final BooleanValue alwaysPotOnSword = addValue(new BooleanValue("Always Pot On Sword", false));
	private final NumberValue minHealth = addValue(new NumberValue("Min Health", 10, 0, 36, 0.5));
	private final BooleanValue elytra = addValue(new BooleanValue("Elytra", false));
	private final BooleanValue falling = addValue(new BooleanValue("Falling", false));
	private final BooleanValue explosion = addValue(new BooleanValue("Explosion", true));

	private int ticks;
	private boolean locked;

	public Offhand() {
		super("Offhand", 0, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		ticks = 0;
		locked = false;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.player.containerMenu == null) {
			return;
		}
		if (ticks++ < (int) delay.getValue()) {
			return;
		}
		ticks = 0;

		Item totem = Items.TOTEM_OF_UNDYING;
		int totemSlot = MeteorCombatHelper.findItemSlot(totem, false);
		locked = totemSlot != -1 && needsTotem();

		if (locked) {
			if (!mc.player.getOffhandItem().is(totem)) {
				moveToOffhand(totemSlot);
			}
			return;
		}

		Item desired = desiredItem();
		if (desired == null) {
			return;
		}
		if (mc.player.getOffhandItem().is(desired)) {
			return;
		}
		int slot = MeteorCombatHelper.findItemSlot(desired, hotbar.getValue());
		if (slot != -1 && slot != 40) {
			moveToOffhand(slot);
		}
	}

	private boolean needsTotem() {
		if (mc.player == null) {
			return false;
		}
		double health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
		if (health <= minHealth.getValue()) {
			return true;
		}
		if (elytra.getValue() && mc.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA) && mc.player.isFallFlying()) {
			return true;
		}
		if (falling.getValue() && mc.player.fallDistance > 3.0f) {
			return true;
		}
		return explosion.getValue() && (mc.player.hurtTime > 0 || crystalNearby());
	}

	private boolean crystalNearby() {
		if (mc.level == null || mc.player == null) {
			return false;
		}
		return !mc.level.getEntities(mc.player, mc.player.getBoundingBox().inflate(6.0),
			entity -> entity instanceof net.minecraft.world.entity.boss.enderdragon.EndCrystal).isEmpty();
	}

	private Item desiredItem() {
		Item base = switch (item.getMode()) {
			case "EGap" -> Items.ENCHANTED_GOLDEN_APPLE;
			case "Gap" -> Items.GOLDEN_APPLE;
			case "Totem" -> Items.TOTEM_OF_UNDYING;
			case "Shield" -> Items.SHIELD;
			case "Potion" -> Items.POTION;
			default -> Items.END_CRYSTAL;
		};
		if (mc.player == null) {
			return base;
		}

		ItemStack main = mc.player.getMainHandItem();
		boolean sword = main.is(ItemTags.SWORDS);
		boolean axe = main.is(ItemTags.AXES);
		boolean rightClick = mc.options.keyUse.isDown();

		if (rightClick && swordGapple.getValue() && sword) {
			return Items.ENCHANTED_GOLDEN_APPLE;
		}
		if ((sword || axe) && alwaysGapOnSword.getValue()) {
			return Items.ENCHANTED_GOLDEN_APPLE;
		}
		if ((sword || axe) && alwaysPotOnSword.getValue()) {
			return Items.POTION;
		}
		return base;
	}

	private void moveToOffhand(int slot) {
		if (mc.player == null || mc.player.containerMenu == null || mc.gameMode == null) {
			return;
		}
		int menuSlot;
		if (slot >= 0 && slot < 9) {
			menuSlot = 36 + slot;
		} else if (slot >= 9 && slot < 36) {
			menuSlot = slot;
		} else {
			return;
		}
		mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, menuSlot, 40, ContainerInput.SWAP, mc.player);
	}
}
