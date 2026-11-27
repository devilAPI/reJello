package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/** Reborn's NoRotateSet only touched the 1.18 position packet fields. 26.x records are immutable. */
public class NoRotateSet extends Module {
	public NoRotateSet() {
		super("NoRotateSet", 0, Category.MISC);
	}
}
