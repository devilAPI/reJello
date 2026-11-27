package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/**
 * Meteor Client camera tweaks. The actual zoom/clip has to happen in {@code Camera.getMaxZoom}; the existing
 * {@code CameraMixin} only handles Camera No Clip, so this class exposes {@link #clip()} and {@link #getDistance()}
 * for that hook. Scrolling needs a mouse-scroll event, which this build does not have either.
 */
public class CameraTweaks extends Module {
	private final BooleanValue clip = addValue(new BooleanValue("Clip", true));
	private final NumberValue cameraDistance = addValue(new NumberValue("Camera Distance", 4, 0, 100, 0.1));
	private final BooleanValue scrolling = addValue(new BooleanValue("Scrolling", true));
	private final NumberValue sensitivity = addValue(new NumberValue("Sensitivity", 1, 0.01, 10, 0.01));

	private double distance = 4;

	public CameraTweaks() {
		super("Camera Tweaks", 0, Category.RENDER);
	}

	@Override
	public void onEnable() {
		distance = cameraDistance.getValue();
	}

	@Override
	public void onUpdate() {
		if (!scrolling.getValue()) {
			distance = cameraDistance.getValue();
		}
	}

	public boolean clip() {
		return isToggled() && clip.getValue();
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = Math.max(0.5, distance);
	}

	public boolean scrolling() {
		return isToggled() && scrolling.getValue();
	}

	public double sensitivity() {
		return sensitivity.getValue();
	}
}
