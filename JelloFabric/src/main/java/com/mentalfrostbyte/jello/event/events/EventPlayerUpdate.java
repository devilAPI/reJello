package com.mentalfrostbyte.jello.event.events;

import com.mentalfrostbyte.jello.event.Event;
import com.mentalfrostbyte.jello.event.EventType;

public class EventPlayerUpdate extends Event {
	private final EventType type;

	public EventPlayerUpdate(EventType type) {
		this.type = type;
	}

	public EventType getType() {
		return type;
	}
}
