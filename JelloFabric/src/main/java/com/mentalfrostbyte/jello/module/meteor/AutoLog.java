package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.player.Player;

/** Meteor Client auto-log: disconnects when health, totem pops or nearby players make survival unsafe. */
public class AutoLog extends Module {
	private final NumberValue health = addValue(new NumberValue("Health", 6, 0, 19, 1));
	private final BooleanValue predict = addValue(new BooleanValue("Predict Incoming Damage", true));
	private final NumberValue totemPops = addValue(new NumberValue("Totem Pops", 0, 0, 100, 1));
	private final BooleanValue onlyTrusted = addValue(new BooleanValue("Only Trusted", false));
	private final NumberValue range = addValue(new NumberValue("Player Range", 64, 1, 128, 1));
	private final BooleanValue toggleOff = addValue(new BooleanValue("Toggle Off", true));

	private int pops;
	private float lastAbsorption;

	public AutoLog() {
		super("Auto Log", 0, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		pops = 0;
		lastAbsorption = mc.player == null ? 0.0f : mc.player.getAbsorptionAmount();
	}

	@EventTarget
	public void onReceive(EventReceivePacket event) {
		if (!isToggled() || mc.level == null || mc.player == null
			|| !(event.getPacket() instanceof ClientboundEntityEventPacket packet)
			|| packet.getEventId() != EntityEvent.PROTECTED_FROM_DEATH) {
			return;
		}
		Entity entity = packet.getEntity(mc.level);
		if (entity != mc.player) {
			return;
		}
		pops++;
		checkTotemPops();
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		// Fallback pop detection: the packet hook in this client does not cover ClientboundEntityEventPacket.
		float absorption = mc.player.getAbsorptionAmount();
		if (absorption >= 6.0f && lastAbsorption < 6.0f && mc.player.getHealth() <= 2.0f) {
			pops++;
			checkTotemPops();
		}
		lastAbsorption = absorption;

		float playerHealth = mc.player.getHealth();
		if (playerHealth <= 0) {
			return;
		}

		if (health.getValue() > 0) {
			if (playerHealth <= health.getValue()) {
				disconnect("Health was lower than " + (int) health.getValue() + ".");
				return;
			}
			if (predict.getValue()) {
				float predicted = playerHealth + mc.player.getAbsorptionAmount() - fallDamage();
				if (predicted < health.getValue()) {
					disconnect("Health was going to be lower than " + (int) health.getValue() + ".");
					return;
				}
			}
		}

		if (onlyTrusted.getValue()) {
			for (Player player : mc.level.players()) {
				if (player == mc.player || player.distanceToSqr(mc.player) > range.getValue() * range.getValue()) {
					continue;
				}
				// No friends API in this client: same scoreboard team is treated as trusted.
				if (mc.player.getTeam() != null && mc.player.getTeam() == player.getTeam()) {
					continue;
				}
				disconnect("Non-trusted player '" + player.getName().getString() + "' appeared nearby.");
				return;
			}
		}
	}

	private void checkTotemPops() {
		if (totemPops.getValue() > 0 && pops >= totemPops.getValue()) {
			disconnect("Popped " + pops + " totems.");
		}
	}

	/** Rough vanilla fall-damage estimate, used for the predictive check. */
	private float fallDamage() {
		if (mc.player == null) {
			return 0;
		}
		return mc.player.fallDistance > 3.0 ? (float) (mc.player.fallDistance - 3.0) : 0.0f;
	}

	private void disconnect(String reason) {
		if (mc.getConnection() == null || mc.getConnection().getConnection() == null) {
			return;
		}
		mc.getConnection().getConnection().disconnect(Component.literal("[AutoLog] " + reason));
		if (toggleOff.getValue()) {
			setToggled(false);
		}
	}
}
