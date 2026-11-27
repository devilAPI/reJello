package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;

/** Cancels position packets with NaN/infinite coordinates. */
public class AntiCrash extends Module {
	public AntiCrash() {
		super("AntiCrash", 0, Category.MISC);
	}

	@EventTarget
	public void onPacket(EventReceivePacket event) {
		if (event.getPacket() instanceof ClientboundPlayerPositionPacket packet) {
			var change = packet.change();
			if (!Double.isFinite(change.position().x) || !Double.isFinite(change.position().y)
				|| !Double.isFinite(change.position().z) || !Float.isFinite(change.yRot())
				|| !Float.isFinite(change.xRot())) {
				event.setCancelled(true);
			}
		}
	}
}
