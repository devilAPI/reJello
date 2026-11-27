package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Echest Farmer). Behaviour port pending. */
public class EChestFarmer extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("self-toggle", false));
	private final BooleanValue value2 = addValue(new BooleanValue("ignore-existing", true));
	private final NumberValue value3 = addValue(new NumberValue("amount", 64, 8, 512, 1));
	private final BooleanValue value4 = addValue(new BooleanValue("swing-hand", true));
	private final BooleanValue value5 = addValue(new BooleanValue("render", true));
	private final ModeValue value6 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));

	public EChestFarmer() {
		super("Echest Farmer", 0, Category.WORLD);
	}
}
