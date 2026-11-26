package com.mentalfrostbyte.jello.mixin;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventType;
import com.mentalfrostbyte.jello.event.events.EventEntityStep;
import com.mentalfrostbyte.jello.event.events.EventPlayerUpdate;
import com.mentalfrostbyte.jello.event.events.EventSlowDown;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	@Inject(method = "aiStep", at = @At("HEAD"))
	private void jello$preUpdate(CallbackInfo ci) {
		EventManager.call(new EventPlayerUpdate(EventType.PRE));
	}

	@Inject(method = "aiStep", at = @At("TAIL"))
	private void jello$postUpdate(CallbackInfo ci) {
		EventManager.call(new EventPlayerUpdate(EventType.POST));
	}

	/** NoSlow: cancels the "using an item" movement multiplier. */
	@Inject(method = "itemUseSpeedMultiplier", at = @At("RETURN"), cancellable = true)
	private void jello$itemUseSpeed(CallbackInfoReturnable<Float> cir) {
		if ((Object) this != Minecraft.getInstance().player) {
			return;
		}
		EventSlowDown e = new EventSlowDown();
		EventManager.call(e);
		if (e.isCancelled()) {
			cir.setReturnValue(1.0F);
		}
	}
}
