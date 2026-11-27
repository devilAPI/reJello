package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/**
 * Meteor Client better beacons: lets the beacon screen pick any effect regardless of the beacon level.
 *
 * <p>Meteor enforces this in a {@code BeaconScreenMixin} that skips the level check in
 * {@code BeaconScreen.BeaconScreenButton.active}. That mixin does not exist here, so the module only exposes
 * {@link #bypassLevelLimit()} for such a hook.</p>
 */
public class BetterBeacons extends Module {
	public BetterBeacons() {
		super("Better Beacons", 0, Category.MISC);
	}

	public boolean bypassLevelLimit() {
		return isToggled();
	}
}
