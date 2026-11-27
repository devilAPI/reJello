package com.mentalfrostbyte.jello.command;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.alt.Alt;
import com.mentalfrostbyte.jello.config.ConfigManager;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.setting.Value;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

/**
 * Dot commands sent from the vanilla chat bar ({@code .help}, {@code .bind}, ...). The original 1.8 build had no
 * command handler because every module was bound in the ClickGUI; the command names here are the usual Jello set.
 */
public final class CommandManager {
	private static final Map<String, Integer> KEYS = new HashMap<>();

	static {
		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 0; i < letters.length(); i++) {
			KEYS.put(String.valueOf(letters.charAt(i)), 4 + i);
		}
		KEYS.put("0", InputConstants.KEY_0);
		for (int i = 1; i <= 9; i++) {
			KEYS.put(String.valueOf(i), 29 + i);
		}
		for (int i = 1; i <= 12; i++) {
			KEYS.put("F" + i, 57 + i);
		}
		KEYS.put("NONE", 0);
		KEYS.put("RSHIFT", InputConstants.KEY_RSHIFT);
		KEYS.put("LSHIFT", InputConstants.KEY_LSHIFT);
		KEYS.put("RCONTROL", InputConstants.KEY_RCONTROL);
		KEYS.put("LCONTROL", InputConstants.KEY_LCONTROL);
		KEYS.put("RALT", InputConstants.KEY_RALT);
		KEYS.put("LALT", InputConstants.KEY_LALT);
		KEYS.put("TAB", InputConstants.KEY_TAB);
		KEYS.put("SPACE", InputConstants.KEY_SPACE);
		KEYS.put("ESCAPE", InputConstants.KEY_ESCAPE);
		KEYS.put("ENTER", InputConstants.KEY_RETURN);
		KEYS.put("BACKSPACE", InputConstants.KEY_BACKSPACE);
		KEYS.put("UP", InputConstants.KEY_UP);
		KEYS.put("DOWN", InputConstants.KEY_DOWN);
		KEYS.put("LEFT", InputConstants.KEY_LEFT);
		KEYS.put("RIGHT", InputConstants.KEY_RIGHT);
		KEYS.put("DELETE", InputConstants.KEY_DELETE);
		KEYS.put("HOME", 74);
		KEYS.put("END", 77);
		KEYS.put("PAGEUP", 75);
		KEYS.put("PAGEDOWN", 78);
		KEYS.put("GRAVE", InputConstants.KEY_GRAVE);
		KEYS.put("MINUS", InputConstants.KEY_MINUS);
		KEYS.put("EQUALS", InputConstants.KEY_EQUALS);
		KEYS.put("SEMICOLON", InputConstants.KEY_SEMICOLON);
		KEYS.put("APOSTROPHE", InputConstants.KEY_APOSTROPHE);
		KEYS.put("COMMA", InputConstants.KEY_COMMA);
		KEYS.put("PERIOD", InputConstants.KEY_PERIOD);
		KEYS.put("SLASH", InputConstants.KEY_SLASH);
	}

	private CommandManager() {
	}

	public static void init() {
		ClientSendMessageEvents.ALLOW_CHAT.register(CommandManager::onChat);
	}

	private static boolean onChat(String message) {
		if (message == null || !message.startsWith(".") || message.length() == 1) {
			return true;
		}
		String body = message.substring(1).trim();
		if (body.isEmpty()) {
			return true;
		}
		String[] args = body.split("\\s+");
		String command = args[0].toLowerCase(Locale.ROOT);
		try {
			switch (command) {
				case "help", "?" -> help();
				case "bind" -> bind(args, true);
				case "unbind" -> bind(args, false);
				case "toggle", "t" -> toggle(args);
				case "modules", "mods" -> modules(args);
				case "enabled" -> enabled();
				case "settings", "values" -> settings(args);
				case "set" -> set(args);
				case "gui", "clickgui" -> gui();
				case "config" -> config(args);
				case "alt" -> alt(args);
				default -> Jello.addChatMessage("Unknown command. Use .help");
			}
		} catch (RuntimeException e) {
			Jello.addChatMessage("Command failed: " + e.getMessage());
		}
		return false;
	}

	private static void help() {
		Jello.addChatMessage("Commands:");
		Jello.addChatMessage(".bind <module> <key>  - bind a module, e.g. .bind Kill Aura X");
		Jello.addChatMessage(".unbind <module>");
		Jello.addChatMessage(".toggle <module>  (alias .t)");
		Jello.addChatMessage(".modules [category]");
		Jello.addChatMessage(".enabled");
		Jello.addChatMessage(".settings <module>");
		Jello.addChatMessage(".set <module> <setting> <value>");
		Jello.addChatMessage(".gui");
		Jello.addChatMessage(".config save|load");
		Jello.addChatMessage(".alt add <name> | .alt list | .alt login <name> | .alt remove <name>");
	}

	private static void bind(String[] args, boolean bind) {
		if (args.length < 2) {
			Jello.addChatMessage(bind ? "Usage: .bind <module> <key>" : "Usage: .unbind <module>");
			return;
		}
		String moduleName = join(args, 1, bind ? args.length - 1 : args.length);
		Module module = Jello.getModule(moduleName);
		if (module == null) {
			Jello.addChatMessage("Unknown module: " + moduleName);
			return;
		}
		int key = 0;
		if (bind) {
			if (args.length < 3) {
				Jello.addChatMessage("Usage: .bind <module> <key>");
				return;
			}
			int keyCode = findKey(args[args.length - 1]);
			if (keyCode < 0) {
				Jello.addChatMessage("Unknown key: " + args[args.length - 1]);
				return;
			}
			key = keyCode;
			moduleName = join(args, 1, args.length - 1);
			module = Jello.getModule(moduleName);
			if (module == null) {
				Jello.addChatMessage("Unknown module: " + moduleName);
				return;
			}
		}
		module.setKeyCode(key);
		ConfigManager.save();
		Jello.addChatMessage(bind ? module.getName() + " bound to " + args[args.length - 1].toUpperCase(Locale.ROOT) : module.getName() + " unbound");
	}

	/** Key names may contain spaces (".bind Kill Aura X"), so the last token is the key and the rest the module. */
	private static int findKey(String token) {
		String name = token.toUpperCase(Locale.ROOT).replace("KEY.KEYBOARD.", "").replace('.', '_');
		Integer mapped = KEYS.get(name);
		if (mapped != null) {
			return mapped;
		}
		try {
			return Integer.parseInt(token);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private static String join(String[] args, int from, int to) {
		StringBuilder builder = new StringBuilder();
		for (int i = from; i < to; i++) {
			if (builder.length() > 0) {
				builder.append(' ');
			}
			builder.append(args[i]);
		}
		return builder.toString();
	}

	private static void toggle(String[] args) {
		if (args.length < 2) {
			Jello.addChatMessage("Usage: .toggle <module>");
			return;
		}
		Module module = Jello.getModule(join(args, 1, args.length));
		if (module == null) {
			Jello.addChatMessage("Unknown module: " + join(args, 1, args.length));
			return;
		}
		module.toggle();
		Jello.addChatMessage(module.getName() + " " + (module.isToggled() ? "enabled" : "disabled"));
	}

	private static void modules(String[] args) {
		if (args.length >= 2) {
			Category category = null;
			for (Category c : Category.values()) {
				if (c.name.equalsIgnoreCase(args[1])) {
					category = c;
				}
			}
			if (category == null) {
				Jello.addChatMessage("Unknown category: " + args[1]);
				return;
			}
			for (Module module : Jello.getModulesInCategory(category)) {
				Jello.addChatMessage(module.getName() + " [" + (module.isToggled() ? "on" : "off") + "]");
			}
			return;
		}
		StringBuilder builder = new StringBuilder();
		for (Module module : Jello.getModules()) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(module.getName());
		}
		Jello.addChatMessage(builder.toString());
	}

	private static void enabled() {
		StringBuilder builder = new StringBuilder();
		for (Module module : Jello.getModules()) {
			if (module.isToggled()) {
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(module.getName());
			}
		}
		Jello.addChatMessage(builder.length() == 0 ? "No modules enabled" : builder.toString());
	}

	private static void settings(String[] args) {
		if (args.length < 2) {
			Jello.addChatMessage("Usage: .settings <module>");
			return;
		}
		Module module = Jello.getModule(join(args, 1, args.length));
		if (module == null) {
			Jello.addChatMessage("Unknown module: " + join(args, 1, args.length));
			return;
		}
		if (module.getValues().isEmpty()) {
			Jello.addChatMessage(module.getName() + " has no settings");
			return;
		}
		for (Value value : module.getValues()) {
			Jello.addChatMessage(module.getName() + " > " + value.getName() + ": " + valueText(value));
		}
	}

	private static void set(String[] args) {
		if (args.length < 4) {
			Jello.addChatMessage("Usage: .set <module> <setting> <value>");
			return;
		}
		Module module = null;
		int moduleEnd = -1;
		for (int k = args.length - 2; k >= 1; k--) {
			Module candidate = Jello.getModule(join(args, 1, k));
			if (candidate != null) {
				module = candidate;
				moduleEnd = k;
				break;
			}
		}
		if (module == null) {
			Jello.addChatMessage("Unknown module");
			return;
		}
		Value setting = null;
		int settingEnd = -1;
		for (int k = args.length - 1; k > moduleEnd; k--) {
			Value candidate = module.getValue(join(args, moduleEnd + 1, k));
			if (candidate != null) {
				setting = candidate;
				settingEnd = k;
				break;
			}
		}
		if (setting == null) {
			Jello.addChatMessage("Unknown setting on " + module.getName());
			return;
		}
		String settingName = setting.getName();
		String valueName = join(args, settingEnd, args.length);
		Value value = setting;
		if (value instanceof BooleanValue bool) {
			bool.set(Boolean.parseBoolean(valueName) || valueName.equalsIgnoreCase("on"));
		} else if (value instanceof NumberValue number) {
			number.setVal(Double.parseDouble(valueName));
		} else if (value instanceof ModeValue mode) {
			mode.setMode(valueName);
		}
		ConfigManager.save();
		Jello.addChatMessage(module.getName() + " > " + value.getName() + " = " + valueText(value));
	}

	private static String valueText(Value value) {
		if (value instanceof BooleanValue bool) {
			return String.valueOf(bool.getValue());
		}
		if (value instanceof NumberValue number) {
			return String.valueOf(number.getValue());
		}
		if (value instanceof ModeValue mode) {
			return mode.getMode();
		}
		return "?";
	}

	private static void gui() {
		if (Jello.jgui == null) {
			Jello.addChatMessage("ClickGUI is not ready");
			return;
		}
		net.minecraft.client.Minecraft.getInstance().execute(() -> net.minecraft.client.Minecraft.getInstance().gui.setScreen(Jello.jgui));
	}

	private static void config(String[] args) {
		if (args.length < 2) {
			Jello.addChatMessage("Usage: .config save|load");
			return;
		}
		if (args[1].equalsIgnoreCase("save")) {
			ConfigManager.save();
			Jello.addChatMessage("Config saved");
		} else if (args[1].equalsIgnoreCase("load")) {
			ConfigManager.load();
			Jello.addChatMessage("Config loaded");
		} else {
			Jello.addChatMessage("Usage: .config save|load");
		}
	}

	private static void alt(String[] args) {
		if (args.length < 2) {
			Jello.addChatMessage("Usage: .alt add|list|login|remove");
			return;
		}
		switch (args[1].toLowerCase(Locale.ROOT)) {
			case "add" -> {
				if (args.length < 3) {
					Jello.addChatMessage("Usage: .alt add <name>");
					return;
				}
				Jello.altManager.add(args[2]);
				Jello.addChatMessage("Added alt " + args[2]);
			}
			case "list" -> {
				if (Jello.altManager.getAlts().isEmpty()) {
					Jello.addChatMessage("No alts");
					return;
				}
				for (Alt alt : Jello.altManager.getAlts()) {
					Jello.addChatMessage((alt.isSelected() ? "* " : "  ") + alt.getUsername());
				}
			}
			case "login" -> {
				if (args.length < 3) {
					Jello.addChatMessage("Usage: .alt login <name>");
					return;
				}
				for (Alt alt : Jello.altManager.getAlts()) {
					if (alt.getUsername().equalsIgnoreCase(args[2])) {
						Jello.altManager.login(alt);
						return;
					}
				}
				Jello.addChatMessage("Unknown alt: " + args[2]);
			}
			case "remove" -> {
				if (args.length < 3) {
					Jello.addChatMessage("Usage: .alt remove <name>");
					return;
				}
				Jello.altManager.getAlts().removeIf(alt -> alt.getUsername().equalsIgnoreCase(args[2]));
				Jello.altManager.save();
				Jello.addChatMessage("Removed alt " + args[2]);
			}
			default -> Jello.addChatMessage("Usage: .alt add|list|login|remove");
		}
	}
}
