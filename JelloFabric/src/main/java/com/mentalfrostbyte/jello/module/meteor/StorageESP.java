package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Storage Esp). Behaviour port pending. */
public class StorageESP extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Shader", "Shader", "C", "T", "B"));
	private final BooleanValue value2 = addValue(new BooleanValue("tracers", false));
	private final ModeValue value3 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));
	private final NumberValue value4 = addValue(new NumberValue("fill-opacity", 50, 0, 255, 1));
	private final NumberValue value5 = addValue(new NumberValue("width", 1, 1, 10, 1));
	private final NumberValue value6 = addValue(new NumberValue("glow-multiplier", 3.5, 0.0, 100, 0.1));
	private final NumberValue value7 = addValue(new NumberValue("fade-distance", 6.0, 0.0, 100, 0.1));
	private final BooleanValue value8 = addValue(new BooleanValue("hide-opened", false));

	public StorageESP() {
		super("Storage Esp", 0, Category.RENDER);
	}
}
