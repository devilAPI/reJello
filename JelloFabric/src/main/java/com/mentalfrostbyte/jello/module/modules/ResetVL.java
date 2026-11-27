package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Anti-cheat flag resetter. Only the local motion reset is meaningful in 26.3. */
public class ResetVL extends Module {
	private final ModeValue type = addValue(new ModeValue("Type", "MatrixTimer", "MatrixTimer", "Hypixel", "Hypixel Safe"));
	private int ticks;

	@Override
	public void onEnable() {
		ticks = 0;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		if (mc.player.onGround()) {
			mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.11, mc.player.getDeltaMovement().z);
			ticks++;
		}
		if (ticks > 25) {
			setToggled(false);
		}
	}

	public ResetVL() {
		super("ResetVL", 0, Category.MISC);
	}
}
