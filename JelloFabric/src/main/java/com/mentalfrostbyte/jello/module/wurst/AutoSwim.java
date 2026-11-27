package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.util.PlayerUtil;

/** Wurst AutoSwim: sprints while swimming so the player moves faster through water. */
public class AutoSwim extends Module {
	private final BooleanValue onlyForward = addValue(new BooleanValue("Only Forward", true));

	public AutoSwim() {
		super("Auto Swim", 0, Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		if (mc.player.horizontalCollision || mc.player.isShiftKeyDown()) {
			return;
		}
		if (mc.player.getAbilities().flying || !mc.player.isInWater()) {
			return;
		}
		if (!mc.player.isSwimming() && !mc.player.isUnderWater()) {
			return;
		}
		if (onlyForward.getValue() && PlayerUtil.forward() <= 0) {
			return;
		}
		mc.player.setSprinting(true);
	}
}
