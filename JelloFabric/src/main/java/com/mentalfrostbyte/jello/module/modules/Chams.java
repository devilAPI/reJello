package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/** 3D chams need a render-pipeline hook; the module is registered with its 1.18 category. */
public class Chams extends Module {
	public Chams() {
		super("Chams", 0, Category.RENDER);
	}
}
