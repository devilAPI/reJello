package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/** The camera tilt is removed by {@link com.mentalfrostbyte.jello.mixin.GameRendererMixin}. */
public class NoHurtCam extends Module {
	public NoHurtCam() {
		super("NoHurtCam", 0, Category.RENDER);
	}
}
