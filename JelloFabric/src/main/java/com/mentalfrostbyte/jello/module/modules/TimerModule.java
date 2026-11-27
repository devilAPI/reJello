package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Client tick-rate modifier. 26.3 owns the tick loop, so the setting is kept for parity. */
public class TimerModule extends Module {
	private final NumberValue timer = addValue(new NumberValue("Timer Speed", 1.0, 0.1, 10.0, 0.1));

	public TimerModule() {
		super("Timer", 0, Category.WORLD);
	}
}
