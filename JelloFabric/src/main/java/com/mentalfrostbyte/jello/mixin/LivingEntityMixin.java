package com.mentalfrostbyte.jello.mixin;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventType;
import com.mentalfrostbyte.jello.event.events.EventEntityStep;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	/** Step module: lets a listener raise the step height for one movement pass. */
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
