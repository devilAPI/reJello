package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Tunnel Esp). Behaviour port pending. */
public class TunnelESP extends Module {
	private final NumberValue value1 = addValue(new NumberValue("height", 0.1, 0, 100, 0.1));
	private final BooleanValue value2 = addValue(new BooleanValue("connected", true));
	private final ModeValue value3 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public TunnelESP() {
		super("Tunnel Esp", 0, Category.RENDER);
	}
}
