package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/**
 * Meteor Client no-rotate: blocks rotations sent by the server in ClientboundPlayerPositionPacket.
 * Meteor implements this in ClientPacketListenerMixin; this build has no hook for that packet, so
 * the module is a flag only until such a mixin is added.
 */
public class NoRotate extends Module {
	public NoRotate() {
		super("No Rotate", 0, Category.PLAYER);
	}
}
