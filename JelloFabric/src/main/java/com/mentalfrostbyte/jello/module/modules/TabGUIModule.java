package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mojang.blaze3d.platform.InputConstants;

/** Only carries the enable state; {@link com.mentalfrostbyte.jello.hud.JelloHud} draws the TabGUI. */
public class TabGUIModule extends Module {
	public TabGUIModule() {
		super("TabGUI", 0, Category.RENDER);
	}
}
