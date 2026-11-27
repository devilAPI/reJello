package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/** Item physicks require an entity-render mixin; registered as in Reborn. */
public class ItemPhysics extends Module {
	public ItemPhysics() {
		super("ItemPhysics", 0, Category.RENDER);
	}
}
