package com.mentalfrostbyte.jello.event.events;

import com.mentalfrostbyte.jello.event.Event;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class EventRender2D extends Event {
	public final GuiGraphicsExtractor graphics;
	public final float partialTicks;

	public EventRender2D(GuiGraphicsExtractor graphics, float partialTicks) {
		this.graphics = graphics;
		this.partialTicks = partialTicks;
	}
}
