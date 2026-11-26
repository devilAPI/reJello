package com.mentalfrostbyte.jello.event.events;

import com.mentalfrostbyte.jello.event.Event;
import net.minecraft.network.protocol.Packet;

public class EventReceivePacket extends Event {
	private final Packet<?> packet;

	public EventReceivePacket(Packet<?> packet) {
		this.packet = packet;
	}

	public Packet<?> getPacket() {
		return packet;
	}
}
