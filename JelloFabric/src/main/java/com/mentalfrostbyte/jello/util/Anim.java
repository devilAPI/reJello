package com.mentalfrostbyte.jello.util;

import net.minecraft.client.Minecraft;

/** Tick-to-frame interpolation used by every Jello animation (the 1.8 code used timer.renderPartialTicks). */
public final class Anim {
	private Anim() {
	}

	public static float partialTicks() {
		return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
	}

	public static float smooth(double current, double last) {
		float pt = partialTicks();
		return (float) (current * pt + last * (1.0f - pt));
	}

	/** Frame-rate scale factor the original used: 5 * debugFPS * 0.04 (= 1 at 5 fps), never zero. */
	public static double fpsFactor(double base) {
		return Math.max(1, base * Math.max(1, Minecraft.getInstance().getFps()) * 0.04);
	}
}
