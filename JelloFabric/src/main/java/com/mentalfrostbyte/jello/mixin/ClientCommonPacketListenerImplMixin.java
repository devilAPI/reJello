package com.mentalfrostbyte.jello.mixin;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;

/** Fires {@link EventPacketSent}; listeners can replace or drop an outgoing packet (Blink, KillAura rotations). */
@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerImplMixin {
	@WrapMethod(method = "send")
	private void jello$send(Packet<?> packet, Operation<Void> original) {
		EventPacketSent e = new EventPacketSent(packet);
		EventManager.call(e);
		if (!e.isCancelled()) {
			original.call(e.getPacket());
		}
	}
}
