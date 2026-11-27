package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/**
 * Wurst Liquids: makes the crosshair target fluids as well as blocks.
 *
 * <p>Not fully ported: this needs a mixin on
 * {@code LocalPlayer.pick(Entity,double,double,float)} that forces the {@code includeFluids}
 * argument of {@code Entity.pick(double,double,boolean)} to true while the module is on. The
 * module is kept as a toggle so that mixin can read {@code isToggled()} later.</p>
 */
public class Liquids extends Module {
	public Liquids() {
		super("Liquids", 0, Category.WORLD);
	}
}
