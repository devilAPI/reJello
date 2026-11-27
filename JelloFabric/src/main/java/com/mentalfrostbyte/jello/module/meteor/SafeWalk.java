package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.PlayerUtil;
import net.minecraft.client.player.LocalPlayer;

/** Meteor Client safe-walk: sneaks at block edges. */
public class SafeWalk extends Module {
	private final NumberValue edgeDistance = addValue(new NumberValue("Edge Distance", 0.6, 0.1, 2.0, 0.1));

	public SafeWalk() {
		super("Safe Walk", 0, Category.MOVEMENT);
	}

	@Override
	public void onDisable() {
		mc.options.keyShift.setDown(false);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null) {
			return;
		}
		mc.options.keyShift.setDown(!PlayerUtil.isNotCollidingBelow(edgeDistance.getValue()));
	}
}
