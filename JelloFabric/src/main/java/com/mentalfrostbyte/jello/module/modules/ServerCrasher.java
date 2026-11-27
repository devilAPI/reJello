package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/** Crash exploit. Deliberately not implemented. */
public class ServerCrasher extends Module {
	public ServerCrasher() {
		super("ServerCrasher", 0, Category.PLAYER);
	}

	@Override
	public void onEnable() {
		com.mentalfrostbyte.jello.Jello.addChatMessage("ServerCrasher is not ported");
		setToggled(false);
	}
}
