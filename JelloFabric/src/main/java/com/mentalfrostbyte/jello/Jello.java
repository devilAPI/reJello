package com.mentalfrostbyte.jello;

import com.mentalfrostbyte.jello.alt.AltManager;
import com.mentalfrostbyte.jello.config.ConfigManager;
import com.mentalfrostbyte.jello.gui.JelloGui;
import com.mentalfrostbyte.jello.hud.JelloHud;
import com.mentalfrostbyte.jello.hud.TabGUI;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.modules.*;
import com.mojang.blaze3d.platform.InputConstants;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Jello {
	public static final String VERSION = "2.0";
	public static final Logger LOGGER = LoggerFactory.getLogger("Jello");
	public static final List<Module> mods = new ArrayList<>();
	public static final TabGUI tabgui = new TabGUI();
	public static final AltManager altManager = new AltManager();
	public static JelloHud hud;
	public static JelloGui jgui;

	private Jello() {
	}

	public static Path getDirectory() {
		return FabricLoader.getInstance().getGameDir().resolve("Jello");
	}

	/** Same registration order as the original client. */
	static void register() {
		register(new AutoArmor());
		register(new AirWalk());
		register(new Notifications());
		register(new InvCleaner());
		register(new KillAura());
		register(new LongJump());
		register(new Compass());
		register(new BlockFly());
		register(new TabGUIModule());
		register(new CameraNoClip());
		register(new KeyStrokes());
		register(new MiniMap());
		register(new AntiKnockback());
		register(new Step());
		register(new NoSlow());
		register(new ActiveMods());
		register(new Fly());
		register(new HighJump());
		register(new BunnyHop());
		register(new ChestStealer());
		register(new NoFall());
		register(new MenuGUI());
		hud = new JelloHud();
		jgui = new JelloGui();
		altManager.load();
		ConfigManager.load();
	}

	public static void register(Module module) {
		mods.add(module);
	}

	public static List<Module> getModules() {
		return mods;
	}

	public static @Nullable Module getModule(String name) {
		String normalized = name.replace(" ", "");
		for (Module m : mods) {
			if (m.getName().equalsIgnoreCase(name) || m.getName().replace(" ", "").equalsIgnoreCase(normalized)) {
				return m;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Module> T getModule(Class<T> type) {
		for (Module m : mods) {
			if (type.isInstance(m)) {
				return (T) m;
			}
		}
		throw new IllegalArgumentException(type.getName());
	}

	/** Missing modules count as disabled (the original referenced modules that never existed, e.g. "Rush", "Fast"). */
	public static boolean isEnabled(String name) {
		Module m = getModule(name);
		return m != null && m.isToggled();
	}

	public static List<Module> getModulesInCategory(Category cat) {
		List<Module> list = new ArrayList<>();
		for (Module m : mods) {
			if (m.category == cat) {
				list.add(m);
			}
		}
		return list;
	}

	public static void onKeyPressed(int keyCode) {
		if (keyCode == 0) {
			return;
		}
		for (Module module : mods) {
			if (module.getKeyCode() == keyCode) {
				module.toggle();
			}
		}
		switch (keyCode) {
			case InputConstants.KEY_UP -> tabgui.keyUp();
			case InputConstants.KEY_DOWN -> tabgui.keyDown();
			case InputConstants.KEY_LEFT -> tabgui.keyLeft();
			case InputConstants.KEY_RIGHT -> tabgui.keyRight();
			default -> {
			}
		}
	}

	public static void onUpdate() {
		for (Module module : mods) {
			if (module.isToggled()) {
				module.onUpdate();
			}
		}
	}

	public static void addChatMessage(String s) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			mc.player.sendSystemMessage(Component.literal("[Jello] §r" + s));
		}
	}
}
