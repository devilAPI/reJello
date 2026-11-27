package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Weather Changer). Behaviour port pending. */
public class WeatherChanger extends Module {
	private final NumberValue value1 = addValue(new NumberValue("rain-level", 0.0, 0.0, 1.0, 0.1));
	private final NumberValue value2 = addValue(new NumberValue("thunder-level", 0.0, 0.0, 1.0, 0.1));

	public WeatherChanger() {
		super("Weather Changer", 0, Category.RENDER);
	}
}
