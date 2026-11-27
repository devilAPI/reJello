package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mojang.blaze3d.platform.InputConstants;

/** Only carries the enable state; {@link com.mentalfrostbyte.jello.hud.JelloHud} draws the TabGUI. */
public class TabGUIModule extends Module {
	private final ModeValue blurMode = addValue(new ModeValue("BlurMode", "Blur", "Blur", "Grad Rect"));

	public TabGUIModule() {
		super("TabGUI", 0, Category.RENDER);
	}
}
