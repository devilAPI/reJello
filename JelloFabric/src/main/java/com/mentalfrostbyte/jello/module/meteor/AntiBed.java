package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** Meteor Client module (Anti Bed). Behaviour port pending. */
public class AntiBed extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("place-string-top", false));
	private final BooleanValue value2 = addValue(new BooleanValue("place-string-middle", true));
	private final BooleanValue value3 = addValue(new BooleanValue("place-string-bottom", false));
	private final BooleanValue value4 = addValue(new BooleanValue("only-in-hole", true));

	public AntiBed() {
		super("Anti Bed", 0, Category.COMBAT);
	}
}
