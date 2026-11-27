package com.mentalfrostbyte.jello.mixin;

import com.mentalfrostbyte.jello.Jello;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	/** NoHurtCam: skip the hurt tilt of the first-person camera. */
	@Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
	private void jello$bobHurt(CallbackInfo ci) {
		if (Jello.isEnabled("NoHurtCam")) {
			ci.cancel();
		}
	}
}
