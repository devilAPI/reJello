package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/**
 * Meteor Client trident-boost: multiplies the riptide push and allows riptide outside of water.
 * Upstream this is consumed by TridentItemMixin (releaseUsing velocity and isInWaterOrRain checks);
 * those mixins are not present in JelloFabric yet, the accessors below are ready for them.
 */
public class TridentBoost extends Module {
	private final NumberValue boost = addValue(new NumberValue("Boost", 2.0, 0.1, 10.0, 0.1));
	private final BooleanValue outOfWater = addValue(new BooleanValue("Out Of Water", true));

	public TridentBoost() {
		super("Trident Boost", 0, Category.MOVEMENT);
	}

	/** Multiplier applied to the riptide push (1 = vanilla). */
	public double getMultiplier() {
		return isToggled() ? boost.getValue() : 1.0;
	}

	public boolean allowOutOfWater() {
		return isToggled() && outOfWater.getValue();
	}
}
