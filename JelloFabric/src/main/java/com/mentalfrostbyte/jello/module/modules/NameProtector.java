package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/** Hides the local player name in chat. The 1.18 build also only flipped a static flag. */
public class NameProtector extends Module {
	public static String name = "JelloSigma";

	public NameProtector() {
		super("NameProtect", 0, Category.RENDER);
	}
}
