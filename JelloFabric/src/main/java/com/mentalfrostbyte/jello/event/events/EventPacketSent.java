package com.mentalfrostbyte.jello.event.events;

import com.mentalfrostbyte.jello.event.Event;
import net.minecraft.network.protocol.Packet;

public class EventPacketSent extends Event {
	private Packet<?> packet;

	public EventPacketSent(Packet<?> packet) {
		this.packet = packet;
	}

	public Packet<?> getPacket() {
		return packet;
	}

	public void setPacket(Packet<?> packet) {
		this.packet = packet;
	}
}
