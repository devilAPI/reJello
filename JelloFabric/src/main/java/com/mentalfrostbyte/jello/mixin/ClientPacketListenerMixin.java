package com.mentalfrostbyte.jello.mixin;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link EventReceivePacket} for the packets the original client cared about.
 * 26.x dispatches each packet to its own handler method, so the specific ones are hooked.
 */
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Inject(method = "handleSetEntityMotion", at = @At("HEAD"), cancellable = true)
	private void jello$setEntityMotion(ClientboundSetEntityMotionPacket packet, CallbackInfo ci) {
		EventReceivePacket e = new EventReceivePacket(packet);
		EventManager.call(e);
		if (e.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "handleExplosion", at = @At("HEAD"), cancellable = true)
	private void jello$explosion(ClientboundExplodePacket packet, CallbackInfo ci) {
		EventReceivePacket e = new EventReceivePacket(packet);
		EventManager.call(e);
		if (e.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "handleContainerSetSlot", at = @At("HEAD"), cancellable = true)
	private void jello$containerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
		EventReceivePacket e = new EventReceivePacket(packet);
		EventManager.call(e);
		if (e.isCancelled()) {
			ci.cancel();
		}
	}
}
