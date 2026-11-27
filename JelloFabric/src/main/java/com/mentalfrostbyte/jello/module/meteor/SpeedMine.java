package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.BlockHitResult;

/** Meteor Client speed-mine: faster mining via a block-break modifier, haste or early damage. */
public class SpeedMine extends Module {
	private static final Identifier MODIFIER_ID = Identifier.fromNamespaceAndPath("jello", "speed_mine");

	private final ModeValue mode = addValue(new ModeValue("Mode", "Damage", "Damage", "Normal", "Haste"));
	private final NumberValue modifier = addValue(new NumberValue("Modifier", 1.4, 0.0, 10.0, 0.1));
	private final NumberValue hasteAmplifier = addValue(new NumberValue("Haste Amplifier", 2, 1, 5, 1));
	private final BooleanValue instamine = addValue(new BooleanValue("Instamine", true));
	private final BooleanValue grimBypass = addValue(new BooleanValue("Grim Bypass", false));

	private double appliedModifier = -1;
	private BlockPos lastDamagePos;
	private boolean sendingGrim;

	public SpeedMine() {
		super("Speed Mine", 0, Category.PLAYER);
	}

	@Override
	public void onDisable() {
		removeHaste();
		clearModifier();
		lastDamagePos = null;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}

		if (mode.is("Haste")) {
			clearModifier();
			applyHaste();
		} else if (mode.is("Normal")) {
			removeHaste();
			applyModifier();
		} else {
			removeHaste();
			clearModifier();
		}

		if (!mode.is("Damage") || !mc.gameMode.isDestroying() || mc.getConnection() == null
			|| !(mc.hitResult instanceof BlockHitResult hit)) {
			lastDamagePos = null;
			return;
		}

		BlockPos pos = hit.getBlockPos();
		int stage = mc.gameMode.getDestroyStage();
		int threshold = instamine.getValue() ? 5 : 7;
		if (stage >= threshold && !pos.equals(lastDamagePos)) {
			lastDamagePos = pos;
			mc.getConnection().send(new ServerboundPlayerActionPacket(
				ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, hit.getDirection()));
		} else if (stage < 0 || mc.level.getBlockState(pos).isAir()) {
			lastDamagePos = null;
		}
	}

	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (!isToggled() || mc.player == null || sendingGrim || !mode.is("Damage") || !grimBypass.getValue()
			|| mc.getConnection() == null) {
			return;
		}
		// Grim fastbreak check workaround, as in Meteor 2.3.58.
		if (event.getPacket() instanceof ServerboundPlayerActionPacket packet
			&& packet.getAction() == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
			sendingGrim = true;
			mc.getConnection().send(new ServerboundPlayerActionPacket(
				ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK,
				packet.getPos().above(), packet.getDirection()));
			sendingGrim = false;
		}
	}

	private void applyHaste() {
		int amplifier = (int) hasteAmplifier.getValue() - 1;
		MobEffectInstance haste = mc.player.getEffect(MobEffects.HASTE);
		if (haste == null || haste.getAmplifier() < amplifier) {
			mc.player.addEffect(new MobEffectInstance(MobEffects.HASTE, -1, amplifier, false, false, false));
		}
	}

	private void removeHaste() {
		if (mc.player == null) {
			return;
		}
		MobEffectInstance haste = mc.player.getEffect(MobEffects.HASTE);
		if (haste != null && !haste.showIcon()) {
			mc.player.removeEffect(MobEffects.HASTE);
		}
	}

	private void applyModifier() {
		AttributeInstance attribute = mc.player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
		if (attribute == null) {
			return;
		}
		double amount = Math.max(0.0, modifier.getValue() - 1.0);
		if (appliedModifier == amount) {
			return;
		}
		attribute.removeModifier(MODIFIER_ID);
		if (amount > 0) {
			attribute.addOrUpdateTransientModifier(new AttributeModifier(MODIFIER_ID, amount,
				AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		}
		appliedModifier = amount;
	}

	private void clearModifier() {
		appliedModifier = -1;
		if (mc.player == null) {
			return;
		}
		AttributeInstance attribute = mc.player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
		if (attribute != null) {
			attribute.removeModifier(MODIFIER_ID);
		}
	}
}
