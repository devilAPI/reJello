package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.PlayerUtil;
import net.minecraft.client.player.LocalPlayer;

/** Meteor Client parkour: jumps at the edge. */
public class Parkour extends Module {
	private final NumberValue edgeDistance = addValue(new NumberValue("Edge Distance", 0.6, 0.1, 2.0, 0.1));

	public Parkour() {
		super("Parkour", 0, Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || !p.onGround() || !PlayerUtil.isMoving()) {
			return;
		}
		if (!PlayerUtil.isNotCollidingBelow(edgeDistance.getValue())) {
			p.jumpFromGround();
		}
	}
}
