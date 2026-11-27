package com.mentalfrostbyte.jello.mixin;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventType;
import com.mentalfrostbyte.jello.event.events.EventEntityStep;
import com.mentalfrostbyte.jello.module.modules.AirWalk;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	/** AirWalk: zero gravity for the local player, like the 1.8 module that forced onGround every tick. */
	@Inject(method = "getEffectiveGravity", at = @At("RETURN"), cancellable = true)
	private void jello$gravity(CallbackInfoReturnable<Double> cir) {
		if ((Object) this == Minecraft.getInstance().player && AirWalk.isEnabled) {
			cir.setReturnValue(0.0);
		}
	}

	@Inject(method = "maxUpStep", at = @At("RETURN"), cancellable = true)
	private void jello$maxUpStep(CallbackInfoReturnable<Float> cir) {
		if ((Object) this != Minecraft.getInstance().player) {
			return;
		}
		float current = cir.getReturnValue();
		EventEntityStep e = new EventEntityStep((Entity) (Object) this, current, EventType.PRE);
		EventManager.call(e);
		if (e.getStepHeight() != current) {
			cir.setReturnValue(e.getStepHeight());
		}
	}
}
