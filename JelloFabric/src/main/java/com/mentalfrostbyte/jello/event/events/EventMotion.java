package com.mentalfrostbyte.jello.event.events;

import com.mentalfrostbyte.jello.event.Event;
import com.mentalfrostbyte.jello.event.EventType;

/** Wraps {@code LocalPlayer.sendPosition()}. PRE values may be changed and are what the server receives. */
public class EventMotion extends Event {
	private final EventType type;
	private boolean onGround;
	private float yaw;
	private float pitch;

	public EventMotion(EventType type, boolean onGround, float yaw, float pitch) {
		this.type = type;
		this.onGround = onGround;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public EventType getType() {
		return type;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
}
