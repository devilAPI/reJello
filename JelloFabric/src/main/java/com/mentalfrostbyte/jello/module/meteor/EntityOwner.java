package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Entity Owner). Behaviour port pending. */
public class EntityOwner extends Module {
	private final NumberValue value1 = addValue(new NumberValue("scale", 1.0, 0.0, 100, 0.1));

	public EntityOwner() {
		super("Entity Owner", 0, Category.RENDER);
	}
}
