package com.mentalfrostbyte.jello.mixin;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Fires {@link EventPacketSent}; a listener can swap the outgoing packet (KillAura's smoothed rotations). */
@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerImplMixin {
	@ModifyVariable(method = "send", at = @At("HEAD"), argsOnly = true)
	private Packet<?> jello$send(Packet<?> packet) {
		EventPacketSent e = new EventPacketSent(packet);
		EventManager.call(e);
		return e.getPacket();
	}
}
