package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.mixin.MultiPlayerGameModeAccessor;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/** Meteor Client break-delay. */
public class BreakDelay extends Module {
	public BreakDelay() {
		super("Break Delay", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (isToggled() && mc.gameMode != null) {
			((MultiPlayerGameModeAccessor) mc.gameMode).jello$setDestroyDelay(0);
		}
	}
}
