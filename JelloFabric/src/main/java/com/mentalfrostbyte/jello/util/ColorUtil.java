package com.mentalfrostbyte.jello.util;

public final class ColorUtil {
	private ColorUtil() {
	}

	/** 50/50 blend of two ARGB colours, like the 1.8 ColorUtil.blend. */
	public static int blend(int a, int b) {
		int r = (((a >> 16) & 0xFF) + ((b >> 16) & 0xFF)) / 2;
		int g = (((a >> 8) & 0xFF) + ((b >> 8) & 0xFF)) / 2;
		int bl = ((a & 0xFF) + (b & 0xFF)) / 2;
		return 0xFF000000 | (r << 16) | (g << 8) | bl;
	}
}
