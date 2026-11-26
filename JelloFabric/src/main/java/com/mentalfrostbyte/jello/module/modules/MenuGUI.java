package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Module;
import com.mojang.blaze3d.platform.InputConstants;

/** Right shift opens the ClickGUI on the next enable pass (the enable hook is deferred outside a world). */
public class MenuGUI extends Module {
	public MenuGUI() {
		super("Menu", InputConstants.KEY_RSHIFT, null);
		addValue(new com.mentalfrostbyte.jello.setting.BooleanValue("Blur", true));
	}

	@Override
	public void onEnable() {
		if (mc.level != null) {
			mc.gui.setScreen(Jello.jgui);
			// The module is a trigger, never left switched on.
			setToggled(false);
		}
	}
}
