package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Waypoints). Behaviour port pending. */
public class WaypointsModule extends Module {
	private final NumberValue value1 = addValue(new NumberValue("text-render-distance", 100, 0, 100, 1));
	private final NumberValue value2 = addValue(new NumberValue("waypoint-fade-distance", 20, 0, 100, 1));
	private final NumberValue value3 = addValue(new NumberValue("max-death-positions", 0, 0, 100, 1));
	private final BooleanValue value4 = addValue(new BooleanValue("chat", false));

	public WaypointsModule() {
		super("Waypoints", 0, Category.RENDER);
	}
}
