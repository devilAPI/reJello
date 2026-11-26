package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;

/** Cancels the knockback packets (explosions and the velocity packet). */
public class AntiKnockback extends Module {
	public static boolean isEnabled;

	public AntiKnockback() {
		super("AntiKnockback", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		isEnabled = true;
	}

	@Override
	public void onDisable() {
		isEnabled = false;
	}

	@EventTarget
	public void onKnockback(EventReceivePacket event) {
		if (event.getPacket() instanceof ClientboundExplodePacket || event.getPacket() instanceof ClientboundSetEntityMotionPacket) {
			event.setCancelled(true);
		}
	}
}
