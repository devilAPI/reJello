package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Hides the local player name in chat. The 1.18 build also only flipped a static flag. */
public class NameProtector extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	public static String name = "JelloSigma";

	public NameProtector() {
		super("NameProtect", 0, Category.RENDER);
	}
}
