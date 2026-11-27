package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Speed Mine). Behaviour port pending. */
public class SpeedMine extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Damage", "Damage", "C", "T", "B"));
	private final ModeValue value2 = addValue(new ModeValue("blocks-filter", "Blacklist", "Blacklist", "W", "B"));
	private final NumberValue value3 = addValue(new NumberValue("modifier", 1.4, 0.0, 100, 0.1));
	private final NumberValue value4 = addValue(new NumberValue("haste-amplifier", 2, 1, 100, 1));
	private final BooleanValue value5 = addValue(new BooleanValue("instamine", true));
	private final BooleanValue value6 = addValue(new BooleanValue("grim-bypass", false));

	public SpeedMine() {
		super("Speed Mine", 0, Category.PLAYER);
	}
}
