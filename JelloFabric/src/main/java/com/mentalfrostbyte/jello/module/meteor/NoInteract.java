package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;

/**
 * Meteor Client no-interact: cancels selected interactions at the packet layer.
 * Meteor uses block/entity type lists; those setting types do not exist here, so this port
 * exposes the hand filters plus the baby/nametag checks and a global block-mining switch.
 */
public class NoInteract extends Module {
	private final BooleanValue blockMining = addValue(new BooleanValue("Block Mining", false));
	private final ModeValue blockInteractHand = addValue(new ModeValue("Block Interact Hand", "None",
		"None", "Mainhand", "Offhand", "Both"));
	private final ModeValue entityInteractHand = addValue(new ModeValue("Entity Interact Hand", "None",
		"None", "Mainhand", "Offhand", "Both"));
	private final BooleanValue babies = addValue(new BooleanValue("Babies", false));
	private final BooleanValue nametagged = addValue(new BooleanValue("Nametagged", false));

	public NoInteract() {
		super("No Interact", 0, Category.PLAYER);
	}

	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (!isToggled() || mc.player == null) {
			return;
		}

		if (event.getPacket() instanceof ServerboundPlayerActionPacket packet) {
			if (blockMining.getValue()
				&& packet.getAction() == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
				event.setCancelled(true);
			}
			return;
		}

		if (event.getPacket() instanceof ServerboundUseItemOnPacket packet) {
			if (handBlocked(packet.hand(), blockInteractHand.getMode())) {
				event.setCancelled(true);
			}
			return;
		}

		if (event.getPacket() instanceof ServerboundInteractPacket packet) {
			if (handBlocked(packet.hand(), entityInteractHand.getMode())
				|| blockedEntity(packet.entityId())) {
				event.setCancelled(true);
			}
			return;
		}

		if (event.getPacket() instanceof ServerboundAttackPacket packet && blockedEntity(packet.entityId())) {
			event.setCancelled(true);
		}
	}

	private boolean blockedEntity(int id) {
		if (mc.level == null) {
			return false;
		}
		Entity entity = mc.level.getEntity(id);
		if (entity == null) {
			return false;
		}
		if (babies.getValue() && entity instanceof Animal animal && animal.isBaby()) {
			return true;
		}
		return nametagged.getValue() && entity.hasCustomName();
	}

	private static boolean handBlocked(InteractionHand hand, String mode) {
		return mode.equals("Both")
			|| (mode.equals("Mainhand") && hand == InteractionHand.MAIN_HAND)
			|| (mode.equals("Offhand") && hand == InteractionHand.OFF_HAND);
	}
}
