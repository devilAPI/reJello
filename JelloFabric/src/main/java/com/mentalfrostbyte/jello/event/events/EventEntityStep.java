package com.mentalfrostbyte.jello.event.events;

import com.mentalfrostbyte.jello.event.Event;
import com.mentalfrostbyte.jello.event.EventType;
import net.minecraft.world.entity.Entity;

public class EventEntityStep extends Event {
	private final Entity entity;
	private float stepHeight;
	private final EventType type;

	public EventEntityStep(Entity entity, float stepHeight, EventType type) {
		this.entity = entity;
		this.stepHeight = stepHeight;
		this.type = type;
	}

	public Entity getEntity() {
		return entity;
	}

	public float getStepHeight() {
		return stepHeight;
	}

	public void setStepHeight(float stepHeight) {
		this.stepHeight = stepHeight;
	}

	public EventType getType() {
		return type;
	}
}
