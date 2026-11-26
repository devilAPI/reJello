package com.mentalfrostbyte.jello.event.events;

import com.mentalfrostbyte.jello.event.Event;

/** Self movement of the local player, before collision. */
public class EventMove extends Event {
	public double x, y, z;

	public EventMove(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}
}
