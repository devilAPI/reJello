package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

/**
 * Meteor Client notifier. Ported: visual-range enter/leave, player join/leave messages and their queue delay.
 * Totem-pop and ender-pearl notifications need {@code ClientboundEntityEventPacket}/{@code ClientboundPlayerInfo*}
 * receive hooks, which the existing packet mixin does not expose; the switches are kept for that future hook.
 */
public class Notifier extends Module {
	private final BooleanValue totemPops = addValue(new BooleanValue("Totem Pops", true));
	private final BooleanValue totemIgnoreOwn = addValue(new BooleanValue("Totem Ignore Own", false));
	private final BooleanValue totemDistanceCheck = addValue(new BooleanValue("Totem Distance Check", false));
	private final NumberValue totemRadius = addValue(new NumberValue("Totem Radius", 30, 1, 100, 1));
	private final BooleanValue visualRange = addValue(new BooleanValue("Visual Range", false));
	private final NumberValue visualRangeDistance = addValue(new NumberValue("Visual Range Distance", 64, 16, 128, 1));
	private final BooleanValue sound = addValue(new BooleanValue("Sound", true));
	private final ModeValue joinsLeaves = addValue(new ModeValue("Player Joins Leaves", "None", "None", "Joins", "Leaves", "Both"));
	private final NumberValue notificationDelay = addValue(new NumberValue("Notification Delay", 0, 0, 100, 1));
	private final BooleanValue simpleNotifications = addValue(new BooleanValue("Simple Notifications", true));

	private final Map<UUID, String> visible = new HashMap<>();
	private final Map<UUID, String> online = new HashMap<>();
	private final Deque<Component> queue = new ArrayDeque<>();
	private final TimerUtil poll = new TimerUtil();
	private int timer;
	private boolean initialized;
	private boolean visualInitialized;

	public Notifier() {
		super("Notifier", 0, Category.MISC);
	}

	@Override
	public void onEnable() {
		visible.clear();
		online.clear();
		queue.clear();
		timer = 0;
		initialized = false;
		visualInitialized = false;
		poll.reset();
	}

	@Override
	public void onDisable() {
		queue.clear();
		timer = 0;
	}

	@Override
	public void onUpdate() {
		if (mc.player == null || mc.level == null) {
			return;
		}
		if (poll.hasTimeElapsed(1000, true)) {
			checkVisualRange();
			checkJoinsLeaves();
		}
		if (!queue.isEmpty() && timer-- <= 0) {
			mc.player.sendSystemMessage(queue.pollFirst());
			timer = (int) notificationDelay.getValue();
		}
	}

	/** Entry point for a future {@code ClientboundEntityEventPacket} hook. */
	public void onTotemPop(Player player) {
		if (!isToggled() || !totemPops.getValue() || player == null) {
			return;
		}
		if (player == mc.player && totemIgnoreOwn.getValue()) {
			return;
		}
		if (totemDistanceCheck.getValue() && mc.player != null && mc.player.distanceTo(player) > totemRadius.getValue()) {
			return;
		}
		mc.player.sendSystemMessage(Component.literal(player.getName().getString() + " popped a totem."));
	}

	private void checkVisualRange() {
		if (!visualRange.getValue() || mc.player == null) {
			return;
		}
		Map<UUID, String> current = new HashMap<>();
		for (Player player : mc.level.players()) {
			if (player != mc.player && mc.player.distanceTo(player) <= visualRangeDistance.getValue()) {
				current.put(player.getUUID(), player.getName().getString());
			}
		}
		if (!visualInitialized) {
			visible.putAll(current);
			visualInitialized = true;
			return;
		}
		for (Map.Entry<UUID, String> entry : current.entrySet()) {
			if (!visible.containsKey(entry.getKey())) {
				queue.addLast(Component.literal(entry.getValue() + " has entered your visual range!"));
				playPing();
			}
		}
		Iterator<Map.Entry<UUID, String>> it = visible.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, String> entry = it.next();
			if (!current.containsKey(entry.getKey())) {
				queue.addLast(Component.literal(entry.getValue() + " has left your visual range!"));
				playPing();
				it.remove();
			}
		}
		visible.putAll(current);
	}

	private void checkJoinsLeaves() {
		if (joinsLeaves.is("None") || mc.getConnection() == null) {
			return;
		}
		Map<UUID, String> current = new HashMap<>();
		mc.getConnection().getOnlinePlayers().forEach(info -> current.put(info.getProfile().id(), info.getProfile().name()));
		if (!initialized) {
			online.putAll(current);
			initialized = true;
			return;
		}
		if (joinsLeaves.is("Joins") || joinsLeaves.is("Both")) {
			for (Map.Entry<UUID, String> entry : current.entrySet()) {
				if (!online.containsKey(entry.getKey())) {
					queue.addLast(joinLeave(entry.getValue(), true));
				}
			}
		}
		if (joinsLeaves.is("Leaves") || joinsLeaves.is("Both")) {
			for (Map.Entry<UUID, String> entry : online.entrySet()) {
				if (!current.containsKey(entry.getKey())) {
					queue.addLast(joinLeave(entry.getValue(), false));
				}
			}
		}
		online.clear();
		online.putAll(current);
	}

	private Component joinLeave(String name, boolean join) {
		if (simpleNotifications.getValue()) {
			return Component.literal(ChatFormatting.GRAY + "[" + (join ? ChatFormatting.GREEN + "+" : ChatFormatting.RED + "-")
				+ ChatFormatting.GRAY + "] " + name);
		}
		return Component.literal(ChatFormatting.WHITE + name + ChatFormatting.GRAY + (join ? " joined." : " left."));
	}

	private void playPing() {
		if (sound.getValue() && mc.player != null) {
			mc.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
		}
	}
}
