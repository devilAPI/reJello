package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Item physicks require an entity-render mixin; registered as in Reborn. */
public class ItemPhysics extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	public ItemPhysics() {
		super("ItemPhysics", 0, Category.RENDER);
	}
}
