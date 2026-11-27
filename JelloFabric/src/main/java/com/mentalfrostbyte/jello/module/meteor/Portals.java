package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/**
 * Meteor Client portals: keeps GUIs usable inside a Nether portal.
 * Meteor patches LocalPlayer.handlePortalTransitionEffect; this build has no hook for it, so the
 * module is a flag only until that mixin is added.
 */
public class Portals extends Module {
	public Portals() {
		super("Portals", 0, Category.PLAYER);
	}
}
