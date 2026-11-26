package com.mentalfrostbyte.jello.mixin;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.events.EventKeyPressed;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires {@link EventKeyPressed} for mouse buttons so the KeyStrokes ripples see clicks. */
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	@Inject(method = "onButton", at = @At("HEAD"))
	private void jello$onButton(long handle, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		if (action != InputConstants.PRESS || mc.gui.screen() != null || mc.player == null) {
			return;
		}
		EventManager.call(new EventKeyPressed(InputConstants.Type.MOUSE.getOrCreate(buttonInfo.button())));
	}
}
