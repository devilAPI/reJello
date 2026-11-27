package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Jesus). Behaviour port pending. */
public class Jesus extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("powder-snow", true));
	private final BooleanValue value2 = addValue(new BooleanValue("ncp-bypass", false));
	private final BooleanValue value3 = addValue(new BooleanValue("slow-down", false));
	private final ModeValue value4 = addValue(new ModeValue("mode", "Solid", "Solid", "C", "T", "B"));
	private final BooleanValue value5 = addValue(new BooleanValue("dip-if-burning", true));
	private final BooleanValue value6 = addValue(new BooleanValue("dip-on-sneak", true));
	private final BooleanValue value7 = addValue(new BooleanValue("dip-on-fall", true));
	private final NumberValue value8 = addValue(new NumberValue("dip-fall-height", 4, 1, 255, 1));
	private final ModeValue value9 = addValue(new ModeValue("mode 2", "Solid", "Solid", "C", "T", "B"));
	private final BooleanValue value10 = addValue(new BooleanValue("dip-if-resistant", true));
	private final BooleanValue value11 = addValue(new BooleanValue("dip-on-sneak 2", true));
	private final BooleanValue value12 = addValue(new BooleanValue("dip-on-fall 2", true));
	private final NumberValue value13 = addValue(new NumberValue("dip-fall-height 2", 4, 1, 255, 1));

	public Jesus() {
		super("Jesus", 0, Category.MOVEMENT);
	}
}
