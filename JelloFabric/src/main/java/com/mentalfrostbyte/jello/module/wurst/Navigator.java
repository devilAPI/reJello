package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/** Wurst Navigator: opens the hack menu. Jello's equivalent is the ClickGUI screen. */
public class Navigator extends Module {
	public Navigator() {
		super("Navigator", 0, Category.MISC);
	}

	@Override
	public void onEnable() {
		if (mc.level != null && mc.gui.screen() == null) {
			mc.gui.setScreen(Jello.jgui);
		}
		setToggled(false);
	}
}
