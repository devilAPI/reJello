package com.mentalfrostbyte.jello.util;

public class TimerUtil {
	private long last = System.currentTimeMillis();

	public boolean hasTimeElapsed(long ms, boolean reset) {
		if (System.currentTimeMillis() - last >= ms) {
			if (reset) {
				reset();
			}
			return true;
		}
		return false;
	}

	public boolean delay(long ms) {
		return System.currentTimeMillis() - last >= ms;
	}

	public void reset() {
		last = System.currentTimeMillis();
	}
}
