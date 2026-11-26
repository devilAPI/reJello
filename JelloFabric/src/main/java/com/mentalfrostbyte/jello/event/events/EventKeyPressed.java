package com.mentalfrostbyte.jello.event.events;

import com.mentalfrostbyte.jello.event.Event;
import com.mojang.blaze3d.platform.InputConstants;

/** A keyboard key or mouse button pressed while no screen is open. */
public class EventKeyPressed extends Event {
	private final InputConstants.Key key;

	public EventKeyPressed(InputConstants.Key key) {
		this.key = key;
	}

	public InputConstants.Key getKey() {
		return key;
	}
}
