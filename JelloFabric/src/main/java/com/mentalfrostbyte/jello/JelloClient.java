package com.mentalfrostbyte.jello;

import com.mentalfrostbyte.jello.command.CommandManager;
import com.mentalfrostbyte.jello.config.ConfigManager;
import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.events.EventJoinServer;
import com.mentalfrostbyte.jello.event.events.EventTick;
import com.mentalfrostbyte.jello.module.Module;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

/** Fabric client entrypoint: registers the module list, the HUD layer and the per-tick hooks. */
public class JelloClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Jello.register();
		CommandManager.init();

		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("jello", "overlay"), Jello.hud::render);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			EventManager.call(new EventTick());
			Jello.onUpdate();
			Jello.jgui.onTick();		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			for (Module module : Jello.getModules()) {
				module.onWorldJoin();
			}
			Jello.hud.onWorldLoad();
			EventManager.call(new EventJoinServer());
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save());
	}
}
