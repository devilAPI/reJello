package com.mentalfrostbyte.jello.alt;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** A stored account. Offline profiles only need the name; the UUID is derived the same way vanilla does. */
public final class Alt {
	private final String username;
	private final UUID uuid;
	private boolean selected;

	public Alt(String username) {
		this(username, offlineUuid(username));
	}

	public Alt(String username, UUID uuid) {
		this.username = username;
		this.uuid = uuid;
	}

	public static UUID offlineUuid(String username) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
	}

	public String getUsername() {
		return username;
	}

	public UUID getUuid() {
		return uuid;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
