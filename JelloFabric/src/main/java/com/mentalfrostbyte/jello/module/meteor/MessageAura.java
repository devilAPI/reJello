package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.entity.player.Player;

/**
 * Meteor Client message aura: whispers every player that comes within range once per session.
 * Meteor reacts to entity-add packets; this build has no such event, so the player list is polled instead.
 */
public class MessageAura extends Module {
	private final ModeValue message = addValue(new ModeValue("Message", "Meteor on Crack!", "Meteor on Crack!", "Hello!", "GG"));
	private final NumberValue range = addValue(new NumberValue("Range", 64, 1, 128, 1));
	private final Set<UUID> sent = new HashSet<>();
	private final TimerUtil timer = new TimerUtil();

	public MessageAura() {
		super("Message Aura", 0, Category.MISC);
	}

	@Override
	public void onEnable() {
		sent.clear();
		timer.reset();
	}

	@Override
	public void onUpdate() {
		if (mc.player == null || mc.level == null || mc.getConnection() == null) {
			return;
		}
		if (!timer.hasTimeElapsed(500, true)) {
			return;
		}
		for (Player player : mc.level.players()) {
			if (player == mc.player || sent.contains(player.getUUID())
				|| mc.player.distanceTo(player) > range.getValue()) {
				continue;
			}
			sent.add(player.getUUID());
			mc.getConnection().sendCommand("msg " + player.getName().getString() + " " + message.getMode());
		}
	}
}
