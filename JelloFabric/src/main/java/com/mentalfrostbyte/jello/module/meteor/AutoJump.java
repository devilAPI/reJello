package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.util.PlayerUtil;
import net.minecraft.client.player.LocalPlayer;

/** Meteor Client auto-jump. */
public class AutoJump extends Module {
	private final BooleanValue onlyWhenMoving = addValue(new BooleanValue("Only When Moving", true));

	public AutoJump() {
		super("Auto Jump", 0, Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || !p.onGround()) {
			return;
		}
		if (!onlyWhenMoving.getValue() || PlayerUtil.isMoving()) {
			p.jumpFromGround();
		}
	}
}
