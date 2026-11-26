package com.mentalfrostbyte.jello.setting;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberValue extends Value {
	private double value;
	private double max;
	private final double min;
	private final double increment;
	/** ClickGUI slider-head hover animation. */
	public float hover = 1;
	public float lastHover = 1;

	public NumberValue(String name, double value, double min, double max, double increment) {
		super(name);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}

	public double getValue() {
		return roundToPlace(value, 2);
	}

	public double getIncrement() {
		return increment;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public void increase() {
		if (roundToPlace(value, 2) < max) {
			value += increment;
		}
	}

	public void decrease() {
		if (roundToPlace(value, 2) > min) {
			value -= increment;
		}
	}

	public void setVal(double v) {
		v = Math.max(min, Math.min(max, v));
		v = Math.round(v * (1.0D / increment)) / (1.0D / increment);
		value = v;
	}

	public static double roundToPlace(double value, int places) {
		return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
	}
}
