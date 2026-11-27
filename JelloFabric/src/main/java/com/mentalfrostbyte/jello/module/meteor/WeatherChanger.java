package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client weather changer. */
public class WeatherChanger extends Module {
	private final ModeValue weather = addValue(new ModeValue("Weather", "Clear", "Clear", "Rain", "Thunder"));

	public WeatherChanger() {
		super("Weather Changer", 0, Category.WORLD);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.level == null) {
			return;
		}
		switch (weather.getMode()) {
			case "Rain" -> {
				mc.level.setRainLevel(1.0f);
				mc.level.setThunderLevel(0.0f);
			}
			case "Thunder" -> {
				mc.level.setRainLevel(1.0f);
				mc.level.setThunderLevel(1.0f);
			}
			default -> {
				mc.level.setRainLevel(0.0f);
				mc.level.setThunderLevel(0.0f);
			}
		}
	}
}
