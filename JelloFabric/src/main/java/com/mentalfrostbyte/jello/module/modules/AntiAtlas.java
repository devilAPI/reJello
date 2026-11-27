package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.hud.NotificationManager;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/** Auto-reporting is not reproduced; the module only clears its list like the 1.18 build. */
public class AntiAtlas extends Module {
	public AntiAtlas() {
		super("AntiAtlas", 0, Category.PLAYER);
	}

	@Override
	public void onEnable() {
		NotificationManager.notify("AntiAtlas", "Reported player list was cleared.", 2000);
	}

	@Override
	public void onDisable() {
		NotificationManager.notify("AntiAtlas", "Reported player list was cleared.", 2000);
	}
}
