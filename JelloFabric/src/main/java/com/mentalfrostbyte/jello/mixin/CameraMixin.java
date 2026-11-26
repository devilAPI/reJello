package com.mentalfrostbyte.jello.mixin;

import com.mentalfrostbyte.jello.Jello;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Camera No Clip: skip the collision clip of the third-person camera. */
@Mixin(Camera.class)
public class CameraMixin {
	@Inject(method = "getMaxZoom", at = @At("HEAD"), cancellable = true)
	private void jello$getMaxZoom(float cameraDist, CallbackInfoReturnable<Float> cir) {
		if (Jello.isEnabled("Camera No Clip")) {
			cir.setReturnValue(cameraDist);
		}
	}
}
