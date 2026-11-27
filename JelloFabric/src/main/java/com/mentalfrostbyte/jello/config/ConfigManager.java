package com.mentalfrostbyte.jello.config;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.setting.Value;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Same plain-text formats as the original SettingsFile: Modules.txt ({@code name:key:toggled}) and Settings.txt
 * ({@code module:value:setting}). Key codes are SDL3 scancodes as used by Minecraft 26.x (not LWJGL2).
 */
public final class ConfigManager {
	private ConfigManager() {
	}

	private static Path modulesFile() {
		return Jello.getDirectory().resolve("Modules.txt");
	}

	private static Path settingsFile() {
		return Jello.getDirectory().resolve("Settings.txt");
	}

	public static void load() {
		try {
			Files.createDirectories(Jello.getDirectory());
			if (Files.exists(modulesFile())) {
				for (String line : Files.readAllLines(modulesFile(), StandardCharsets.UTF_8)) {
					String[] split = line.split(":");
					if (split.length < 3) {
						continue;
					}
					Module m = Jello.getModule(split[0]);
					if (m == null) {
						continue;
					}
					try {
						m.keyCode = Integer.parseInt(split[1]);
					} catch (NumberFormatException ignored) {
					}
					// Applied without onEnable(): there is no world yet. Module hooks only run while in game.
					if (Boolean.parseBoolean(split[2]) && !m.isToggled()) {
						m.setToggled(true);
					}
				}
			}
			if (Files.exists(settingsFile())) {
				for (String line : Files.readAllLines(settingsFile(), StandardCharsets.UTF_8)) {
					String[] split = line.split(":");
					if (split.length < 3) {
						continue;
					}
					Module m = Jello.getModule(split[0]);
					Value v = m == null ? null : m.getValue(split[1]);
					try {
						if (v instanceof BooleanValue b) {
							b.set(Boolean.parseBoolean(split[2]));
						} else if (v instanceof NumberValue n) {
							n.setVal(Double.parseDouble(split[2]));
						} else if (v instanceof ModeValue mode) {
							mode.setMode(split[2]);
						}
					} catch (NumberFormatException ignored) {
					}
				}
			}
		} catch (IOException e) {
			Jello.LOGGER.error("Failed to load config", e);
		}
	}

	public static void save() {
		List<String> modules = new ArrayList<>();
		List<String> settings = new ArrayList<>();
		for (Module m : Jello.getModules()) {
			modules.add(m.getName() + ":" + m.getKeyCode() + ":" + m.isToggled());
			for (Value v : m.getValues()) {
				if (v instanceof BooleanValue b) {
					settings.add(m.getName() + ":" + b.getName() + ":" + b.isEnabled());
				} else if (v instanceof NumberValue n) {
					settings.add(m.getName() + ":" + n.getName() + ":" + n.getValue());
				} else if (v instanceof ModeValue mode) {
					settings.add(m.getName() + ":" + mode.getName() + ":" + mode.getMode());
				}
			}
		}
		try {
			Files.createDirectories(Jello.getDirectory());
			Files.write(modulesFile(), modules, StandardCharsets.UTF_8);
			Files.write(settingsFile(), settings, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Jello.LOGGER.error("Failed to save config", e);
		}
	}
}
