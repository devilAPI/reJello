package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/** Keeps the ridden entity on top of the player. */
public class RiderGod extends Module {
	@Override
	public void onEnable() {
		if (mc.player != null) {
			if (!mc.player.isPassenger()) {
				com.mentalfrostbyte.jello.Jello.addChatMessage("You are not riding an entity");
				setToggled(false);
			}
		}
	}

	@Override
	public void onUpdate() {
		if (isToggled() && mc.player != null && mc.player.getVehicle() != null) {
			mc.player.getVehicle().setPos(mc.player.getX(), mc.player.getY(), mc.player.getZ());
		}
	}

	public RiderGod() {
		super("RiderGod", 0, Category.MISC);
	}
}
