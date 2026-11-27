package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;

/** Cancels the knockback packets (explosions and the velocity packet). */
public class AntiKnockback extends Module {
	public static boolean isEnabled;
	private final ModeValue mode = addValue(new ModeValue("Mode", "Reverse", "Cancel", "Reverse", "MatrixSimple", "Spoof",
		"MatrixReverse", "Normal", "Hypixel", "Legit", "MatrixAdvanced"));
	private final NumberValue hMotion = addValue(new NumberValue("H Motion", 0, 0, 100, 5));
	private final NumberValue vMotion = addValue(new NumberValue("V Motion", 0, 0, 100, 5));

	public AntiKnockback() {
		super("AntiKnockback", 0, Category.COMBAT);
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
			if (mode.is("Reverse") && mc.player != null && hMotion.getValue() > 0) {
				net.minecraft.world.phys.Vec3 motion = mc.player.getDeltaMovement();
				mc.player.setDeltaMovement(-motion.x, vMotion.getValue() / 100.0, -motion.z);
			}
		}
	}
}
