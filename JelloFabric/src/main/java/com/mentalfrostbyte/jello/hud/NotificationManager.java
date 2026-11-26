package com.mentalfrostbyte.jello.hud;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public final class NotificationManager {
	static @Nullable Notification current;

	private NotificationManager() {
	}

	public static void notify(String text, String secondText, int time) {
		notify(text, secondText, time, null);
	}

	/** Like the original, a new notification only replaces the current one once it has finished. */
	public static void notify(String text, String secondText, int time, @Nullable Identifier image) {
		if (current == null || current.isDone()) {
			current = new Notification(text, secondText, time, image);
		}
	}

	/** Replaces whatever is showing (used for "Now Playing" which must not be swallowed). */
	public static void force(String text, String secondText, int time, @Nullable Identifier image) {
		current = new Notification(text, secondText, time, image);
	}

	public static @Nullable Notification current() {
		return current;
	}
}
