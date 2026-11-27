package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Breadcrumbs). Behaviour port pending. */
public class Breadcrumbs extends Module {
	private final NumberValue value1 = addValue(new NumberValue("max-sections", 1000, 1, 5000, 1));
	private final NumberValue value2 = addValue(new NumberValue("section-length", 0.5, 0.0, 100, 0.1));

	public Breadcrumbs() {
		super("Breadcrumbs", 0, Category.RENDER);
	}
}
