package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

/** Meteor Client auto nametag: uses a hotbar name tag on the closest entity without one. */
public class AutoNametag extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 5.0, 0, 16, 0.5));
	private final ModeValue priority = addValue(new ModeValue("Priority", "Lowest Distance", "Lowest Distance", "Highest Distance"));
	private final BooleanValue renametag = addValue(new BooleanValue("Rename Tag", true));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));

	private final Map<Entity, Integer> cooldowns = new HashMap<>();

	public AutoNametag() {
		super("Auto Nametag", 0, Category.WORLD);
	}

	@Override
	public void onDisable() {
		cooldowns.clear();
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}

		for (Iterator<Map.Entry<Entity, Integer>> it = cooldowns.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<Entity, Integer> entry = it.next();
			if (entry.getValue() <= 1) {
				it.remove();
			} else {
				entry.setValue(entry.getValue() - 1);
			}
		}

		int slot = findNameTag();
		if (slot == -1) {
			return;
		}
		ItemStack tag = mc.player.getInventory().getItem(slot);

		Entity target = null;
		double best = priority.is("Highest Distance") ? -1.0 : Double.MAX_VALUE;
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (entity == mc.player || !(entity instanceof LivingEntity) || !entity.isAlive()) {
				continue;
			}
			double distance = entity.distanceToSqr(mc.player);
			if (distance > range.getValue() * range.getValue()) {
				continue;
			}
			if (cooldowns.containsKey(entity)) {
				continue;
			}
			if (entity.hasCustomName()) {
				if (!renametag.getValue() || entity.getCustomName().equals(tag.getHoverName())) {
					continue;
				}
			}
			if (priority.is("Highest Distance") ? distance > best : distance < best) {
				best = distance;
				target = entity;
			}
		}

		if (target == null) {
			return;
		}
		if (rotate.getValue()) {
			lookAt(target);
		}

		int previous = mc.player.getInventory().getSelectedSlot();
		swap(slot);
		mc.gameMode.interact(mc.player, target,
			new EntityHitResult(target, target.getBoundingBox().getCenter()), InteractionHand.MAIN_HAND);
		swap(previous);
		cooldowns.put(target, 20);
	}

	private int findNameTag() {
		for (int i = 0; i < 9; i++) {
			if (mc.player.getInventory().getItem(i).getItem() == Items.NAME_TAG) {
				return i;
			}
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

	private void lookAt(Entity entity) {
		double dx = entity.getX() - mc.player.getX();
		double dy = entity.getEyeY() - mc.player.getEyeY();
		double dz = entity.getZ() - mc.player.getZ();
		mc.player.setYRot((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
		mc.player.setXRot((float) (-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)))));
	}
}
