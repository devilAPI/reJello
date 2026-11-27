package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Meteor Client fake-player: spawns a client-side player for testing combat/rendering. */
public class FakePlayer extends Module {
	private final BooleanValue copyInv = addValue(new BooleanValue("Copy Inventory", true));
	private final NumberValue health = addValue(new NumberValue("Health", 20, 1, 100, 1));

	private RemotePlayer fakePlayer;

	public FakePlayer() {
		super("Fake Player", 0, Category.PLAYER);
	}

	@Override
	public void onEnable() {
		remove();
		if (mc.player == null || mc.level == null) {
			return;
		}

		RemotePlayer player = new RemotePlayer(mc.level, new GameProfile(UUID.randomUUID(), "FakePlayer"));
		int id = 100_000;
		while (mc.level.getEntity(id) != null) {
			id++;
		}
		player.setId(id);
		player.setPos(mc.player.getX(), mc.player.getY(), mc.player.getZ());
		player.setYRot(mc.player.getYRot());
		player.setXRot(mc.player.getXRot());
		player.setYHeadRot(mc.player.getYRot());

		if (player.getAttribute(Attributes.MAX_HEALTH) != null) {
			player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health.getValue());
		}
		player.setHealth((float) health.getValue());

		if (copyInv.getValue()) {
			player.getInventory().replaceWith(mc.player.getInventory());
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slot.isArmor() || slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
					player.setItemSlot(slot, mc.player.getItemBySlot(slot).copy());
				}
			}
		}

		mc.level.addEntity(player);
		fakePlayer = player;
	}

	@Override
	public void onDisable() {
		remove();
	}

	private void remove() {
		if (fakePlayer != null) {
			fakePlayer.discard();
			fakePlayer = null;
		}
	}
}
