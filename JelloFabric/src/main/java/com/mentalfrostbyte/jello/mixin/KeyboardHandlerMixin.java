package com.mentalfrostbyte.jello.mixin;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.events.EventKeyPressed;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires {@link EventKeyPressed} for keys pressed while no screen is open. */
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
	@Inject(method = "keyPress", at = @At("HEAD"))
	private void jello$keyPress(long handle, int action, KeyEvent event, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		if (action != InputConstants.PRESS || mc.gui.screen() != null || mc.player == null) {
			return;
		}
		EventKeyPressed e = new EventKeyPressed(InputConstants.getKey(event));
		EventManager.call(e);
		if (!e.isCancelled()) {
			Jello.onKeyPressed(event.key());
		}
	}
}
