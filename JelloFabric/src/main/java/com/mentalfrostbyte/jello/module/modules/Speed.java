package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMove;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.PlayerUtil;

/** Horizontal speed multiplier. The mode only changes the flavour text in the 1.18 build. */
public class Speed extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	private final NumberValue speed = addValue(new NumberValue("Speed", 1.0, 0.1, 20.0, 0.1));
	private final ModeValue mode = addValue(new ModeValue("Mode", "Vanilla", "Vanilla", "MatrixSemiStrafe", "MatrixTimerBalance",
		"MatrixMultiply", "MatrixDynamic", "Strafe", "MatrixLow", "BuzzSlow", "NCP", "BlocksMC"));
	private final NumberValue ncpTimer = addValue(new NumberValue("NCPTimer", 1.2, 1.0, 1.5, 0.1));
	private final BooleanValue motionReset = addValue(new BooleanValue("Motion Reset", true));
	private final BooleanValue timerBalance = addValue(new BooleanValue("TimerBalance", false));
	private final BooleanValue autoDisable = addValue(new BooleanValue("AutoDisable", true));
	private final BooleanValue buzzDebug = addValue(new BooleanValue("BuzzSlow Debug", true));

	public Speed() {
		super("Speed", 0, Category.MOVEMENT);
	}

	@EventTarget
	public void onMove(EventMove event) {
		if (!isToggled() || mc.player == null || !PlayerUtil.isMoving()) {
			return;
		}
		double base = PlayerUtil.getBaseMoveSpeed() * Math.max(0.1, speed.getValue());
		double[] motion = PlayerUtil.motionFor(base);
		event.x = motion[0];
		event.z = motion[1];
	}
}
