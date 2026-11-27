package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.world.phys.HitResult;

/**
 * Meteor Client liquid-interact: makes liquids clickable by re-picking with fluids included.
 * Meteor injects into LocalPlayer.pick; this build has no mixin for it, so the same result is
 * produced by refreshing the client hit result after the vanilla pick (HUD stage).
 */
public class LiquidInteract extends Module {
	public LiquidInteract() {
		super("Liquid Interact", 0, Category.PLAYER);
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (mc.hitResult != null && mc.hitResult.getType() != HitResult.Type.MISS) {
			return;
		}
		mc.hitResult = mc.getCameraEntity().pick(mc.player.blockInteractionRange(), event.partialTicks, true);
	}
}
