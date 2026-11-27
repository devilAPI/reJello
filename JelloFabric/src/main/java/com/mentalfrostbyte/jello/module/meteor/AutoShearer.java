package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

/** Meteor Client auto shearer: shears the closest unsheared sheep. */
public class AutoShearer extends Module {
	private final NumberValue distance = addValue(new NumberValue("Distance", 5.0, 0, 16, 0.5));
	private final BooleanValue antiBreak = addValue(new BooleanValue("Anti Break", false));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));

	public AutoShearer() {
		super("Auto Shearer", 0, Category.WORLD);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}

		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof Sheep sheep) || !sheep.isAlive() || sheep.isSheared() || sheep.isBaby()) {
				continue;
			}
			if (sheep.distanceToSqr(mc.player) > distance.getValue() * distance.getValue()) {
				continue;
			}

			int slot = findShears();
			if (slot == -1) {
				return;
			}
			if (rotate.getValue()) {
				lookAt(sheep);
			}

			int previous = mc.player.getInventory().getSelectedSlot();
			swap(slot);
			mc.gameMode.interact(mc.player, sheep,
				new EntityHitResult(sheep, sheep.getBoundingBox().getCenter()), InteractionHand.MAIN_HAND);
			swap(previous);
			return;
		}
	}

	private int findShears() {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (stack.getItem() != Items.SHEARS) {
				continue;
			}
			if (antiBreak.getValue() && stack.getDamageValue() >= stack.getMaxDamage() - 1) {
				continue;
			}
			return i;
		}
		return -1;
	}

	private void swap(int slot) {
		if (mc.player.getInventory().getSelectedSlot() == slot) {
			return;
		}
		mc.player.getInventory().setSelectedSlot(slot);
		if (mc.getConnection() != null) {
			mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
		}
	}

	private void lookAt(Sheep sheep) {
		double dx = sheep.getX() - mc.player.getX();
		double dy = sheep.getEyeY() - mc.player.getEyeY();
		double dz = sheep.getZ() - mc.player.getZ();
		mc.player.setYRot((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
		mc.player.setXRot((float) (-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)))));
	}
}
