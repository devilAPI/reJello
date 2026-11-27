package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client rotation: locks or snaps the yaw and pitch. */
public class Rotation extends Module {
	private final ModeValue yawMode = addValue(new ModeValue("Yaw Lock Mode", "Simple", "Simple", "Smart", "None"));
	private final NumberValue yawAngle = addValue(new NumberValue("Yaw Angle", 0, 0, 360, 0.1));
	private final ModeValue pitchMode = addValue(new ModeValue("Pitch Lock Mode", "Simple", "Simple", "Smart", "None"));
	private final NumberValue pitchAngle = addValue(new NumberValue("Pitch Angle", 0, -90, 90, 0.1));

	public Rotation() {
		super("Rotation", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}

		if (yawMode.is("Simple")) {
			setYaw((float) yawAngle.getValue());
		} else if (yawMode.is("Smart")) {
			setYaw(Math.round((mc.player.getYRot() + 1.0F) / 45.0F) * 45.0F);
		}

		if (pitchMode.is("Simple")) {
			mc.player.setXRot((float) pitchAngle.getValue());
		} else if (pitchMode.is("Smart")) {
			mc.player.setXRot(Math.round((mc.player.getXRot() + 1.0F) / 30.0F) * 30.0F);
		}
	}

	private void setYaw(float yaw) {
		mc.player.setYRot(yaw);
		mc.player.setYHeadRot(yaw);
		mc.player.setYBodyRot(yaw);
	}
}
