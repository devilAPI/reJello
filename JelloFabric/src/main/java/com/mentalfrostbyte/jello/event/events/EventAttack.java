package com.mentalfrostbyte.jello.event.events;

import com.mentalfrostbyte.jello.event.Event;
import net.minecraft.world.entity.Entity;

/** Fired right before the local player attacks an entity (1.18 Reborn EventAttack). */
public class EventAttack extends Event {
	private final Entity target;

	public EventAttack(Entity target) {
		this.target = target;
	}

	public Entity getTarget() {
		return target;
	}
}
