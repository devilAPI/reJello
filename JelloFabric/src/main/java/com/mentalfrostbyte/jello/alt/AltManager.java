package com.mentalfrostbyte.jello.alt;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.mixin.MinecraftAccessor;
import com.mojang.authlib.minecraft.UserApiService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

/**
 * Stores the account list in {@code Jello/Alts.txt}. Mojang password login was retired in 2022, so signing in
 * switches the session to an offline profile (valid on offline-mode servers); Microsoft accounts go through
 * the launcher.
 */
public class AltManager {
	private final List<Alt> alts = new ArrayList<>();

	public AltManager() {
	}

	public List<Alt> getAlts() {
		return alts;
	}

	private Path file() {
		return Jello.getDirectory().resolve("Alts.txt");
	}

	public void load() {
		alts.clear();
		Path path = file();
		if (!Files.exists(path)) {
			return;
		}
		try {
			for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
				String[] split = line.split(":", 3);
				if (split.length < 1 || split[0].isBlank()) {
					continue;
				}
				Alt alt = new Alt(split[0]);
				if (split.length > 1) {
					try {
						alt.setSelected(Boolean.parseBoolean(split[1]));
					} catch (IllegalArgumentException ignored) {
					}
				}
				alts.add(alt);
			}
		} catch (IOException e) {
			Jello.LOGGER.error("Failed to load alts", e);
		}
	}

	public void save() {
		List<String> lines = new ArrayList<>();
		for (Alt alt : alts) {
			lines.add(alt.getUsername() + ":" + alt.isSelected());
		}
		try {
			Files.createDirectories(Jello.getDirectory());
			Files.write(file(), lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Jello.LOGGER.error("Failed to save alts", e);
		}
	}

	public void add(String username) {
		if (username == null || username.isBlank()) {
			return;
		}
		for (Alt alt : alts) {
			if (alt.getUsername().equalsIgnoreCase(username)) {
				return;
			}
		}
		alts.add(new Alt(username.trim()));
		save();
	}

	public void remove(Alt alt) {
		alts.remove(alt);
		save();
	}

	/** Switches the running game to the offline profile with the given name. */
	public void login(Alt alt) {
		Minecraft mc = Minecraft.getInstance();
		User user = new User(alt.getUsername(), alt.getUuid(), "0", Optional.empty(), Optional.empty());
		((MinecraftAccessor) mc).jello$setUser(user);
		((MinecraftAccessor) mc).jello$setUserApiService(UserApiService.OFFLINE);
		for (Alt other : alts) {
			other.setSelected(other == alt);
		}
		save();
		Jello.addChatMessage("Switched to offline profile " + alt.getUsername() + ". Reconnect to apply it.");
	}
}
