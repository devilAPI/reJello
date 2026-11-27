package com.mentalfrostbyte.jello;

import com.mentalfrostbyte.jello.alt.AltManager;
import com.mentalfrostbyte.jello.config.ConfigManager;
import com.mentalfrostbyte.jello.gui.JelloGui;
import com.mentalfrostbyte.jello.hud.JelloHud;
import com.mentalfrostbyte.jello.hud.TabGUI;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.meteor.*;
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

	/** The Reborn 1.18 module list, plus the legacy Jello modules that 1.18 dropped. */
	static void register() {
		register(new AutoArmor());
		register(new AirWalk());
		register(new Notifications());
		register(new InvManager());
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
		register(new Chams());
		register(new Step());
		register(new NoSlow());
		register(new ActiveMods());
		register(new Fly());
		register(new HighJump());
		register(new Speed());
		register(new ChestStealer());
		register(new NoFall());
		register(new MenuGUI());
		register(new ESP());
		register(new Animations());
		register(new TargetHUD());
		register(new Sprint());
		register(new AntiVoid());
		register(new InvMove());
		register(new SigMeme());
		register(new TargetStrafe());
		register(new InfiniteAura());
		register(new Blink());
		register(new AutoClicker());
		register(new FastUse());
		register(new Criticals());
		register(new Tracers());
		register(new ItemPhysics());
		register(new AntiBot());
		register(new Eagle());
		register(new AntiCheat());
		register(new AutoRegister());
		register(new WorldTime());
		register(new NoHurtCam());
		register(new AutoPot());
		register(new AutoTool());
		register(new ResetVL());
		register(new HvHAura());
		register(new Regen());
		register(new NameProtector());
		register(new ServerCrasher());
		register(new AntiCrash());
		register(new Nuker());
		register(new AutoPlay());
		register(new ClickTP());
		register(new Freecam());
		register(new Phase());
		register(new Disabler());
		register(new TestRenderModule());
		register(new JelloBot());
		register(new FastSneak());
		register(new Derp());
		register(new TimerModule());
		register(new AimAssist());
		register(new FullBright());
		register(new AntiAtlas());
		register(new FastPlace());
		register(new RiderGod());
		register(new Idk());
		register(new ClickGUIModule());
		register(new BunnyHop());
		register(new AirJump());
		register(new AirPlace());
		register(new Ambience());
		register(new Anchor());
		register(new AnchorAura());
		register(new AntiAFK());
		register(new AntiAnchor());
		register(new AntiAnvil());
		register(new AntiBed());
		register(new AntiHunger());
		register(new AntiPacketKick());
		register(new ArrowDodge());
		register(new AttributeSwap());
		register(new AutoAnvil());
		register(new AutoBreed());
		register(new AutoBrewer());
		register(new AutoCity());
		register(new AutoEXP());
		register(new AutoEat());
		register(new AutoFish());
		register(new AutoGap());
		register(new AutoJump());
		register(new AutoLog());
		register(new AutoMend());
		register(new AutoMount());
		register(new AutoNametag());
		register(new AutoReconnect());
		register(new AutoReplenish());
		register(new AutoRespawn());
		register(new AutoShearer());
		register(new AutoSign());
		register(new AutoSmelter());
		register(new AutoTotem());
		register(new AutoTrap());
		register(new AutoWalk());
		register(new AutoWasp());
		register(new AutoWeapon());
		register(new AutoWeb());
		register(new BedAura());
		register(new BetterBeacons());
		register(new BetterChat());
		register(new BetterTab());
		register(new BetterTooltips());
		register(new BlockESP());
		register(new BlockSelection());
		register(new Blur());
		register(new BookBot());
		register(new BossStack());
		register(new BowAimbot());
		register(new BowSpam());
		register(new Breadcrumbs());
		register(new BreakDelay());
		register(new BreakIndicators());
		register(new BuildHeight());
		register(new Burrow());
		register(new CameraTweaks());
		register(new ChestSwap());
		register(new CityESP());
		register(new Collisions());
		register(new CrystalAura());
		register(new DiscordPresence());
		register(new EChestFarmer());
		register(new EXPThrower());
		register(new ElytraBoost());
		register(new ElytraFly());
		register(new EndermanLook());
		register(new EntityControl());
		register(new EntityOwner());
		register(new Excavator());
		register(new FakePlayer());
		register(new FastClimb());
		register(new Flamethrower());
		register(new FreeLook());
		register(new GhostHand());
		register(new HandView());
		register(new HighwayBuilder());
		register(new Hitboxes());
		register(new HoleESP());
		register(new HoleFiller());
		register(new InfinityMiner());
		register(new InstantRebreak());
		register(new InventoryTweaks());
		register(new ItemHighlight());
		register(new Jesus());
		register(new LightOverlay());
		register(new LiquidFiller());
		register(new LiquidInteract());
		register(new LogoutSpots());
		register(new Marker());
		register(new MessageAura());
		register(new MiddleClickExtra());
		register(new Multitask());
		register(new Nametags());
		register(new NoGhostBlocks());
		register(new NoInteract());
		register(new NoMiningTrace());
		register(new NoRender());
		register(new NoRotate());
		register(new NoStatusEffects());
		register(new Notebot());
		register(new Notifier());
		register(new Offhand());
		register(new PacketCanceller());
		register(new PacketLogger());
		register(new PacketMine());
		register(new Parkour());
		register(new PopChams());
		register(new Portals());
		register(new PotionSaver());
		register(new Quiver());
		register(new ReverseStep());
		register(new Rotation());
		register(new SafeWalk());
		register(new SelfAnvil());
		register(new SelfTrap());
		register(new SelfWeb());
		register(new ServerSpoof());
		register(new Slippy());
		register(new SoundBlocker());
		register(new Spam());
		register(new SpawnProofer());
		register(new SpeedMine());
		register(new Spider());
		register(new StashFinder());
		register(new StorageESP());
		register(new Surround());
		register(new Swarm());
		register(new TimeChanger());
		register(new Trail());
		register(new Trajectories());
		register(new TridentBoost());
		register(new TunnelESP());
		register(new VeinMiner());
		register(new VoidESP());
		register(new WallHack());
		register(new WaypointsModule());
		register(new WeatherChanger());
		register(new Xray());
		register(new Zoom());
		hud = new JelloHud();
		jgui = new JelloGui();
		altManager.load();
		// Fresh installs get the TabGUI HUD without having to open the ClickGUI first.
		Module tab = getModule("TabGUI");
		if (tab != null) {
			tab.setToggled(true);
		}
		ConfigManager.load();
		// The GUI triggers are never valid persistent states.
		for (String trigger : new String[] {"Menu", "ClickGUI"}) {
			Module module = getModule(trigger);
			if (module != null) {
				module.setToggled(false);
			}
		}
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
