package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/** The Reborn "RenderModule" is named "Test" and only exposes the first-person flag. */
public class TestRenderModule extends Module {
	private final BooleanValue firstPerson = addValue(new BooleanValue("ShowInFirstPerson", false));

	public TestRenderModule() {
		super("Test", 0, Category.RENDER);
	}
}
