package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.EventType;
import com.mentalfrostbyte.jello.event.events.EventEntityStep;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** 1.18 Step (NCP mode) on top of the 26.3 max-up-step hook. */
public class Step extends Module {
	public static boolean enabled;
	private final ModeValue mode = addValue(new ModeValue("Mode", "NCP", "NCP"));
	private double preY;

	public Step() {
		super("Step", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		enabled = true;
	}

	@Override
	public void onDisable() {
		enabled = false;
	}

	@EventTarget
	public void onStep(EventEntityStep event) {
		if (mc.player == null || event.getEntity() != mc.player || mc.player.isInWater() || mc.player.isInLava()) {
			return;
		}
		if (event.getType() == EventType.PRE) {
			preY = mc.player.getY();
			if (mc.player.onGround() && mc.player.getDeltaMovement().y < 0 && !mc.player.input.keyPresses.jump()) {
				event.setStepHeight(1.5f);
			}
		}
	}

	@Override
	public void onUpdate() {
		if (isToggled() && mc.player != null && mc.player.onGround()) {
			preY = mc.player.getY();
		}
	}
}
