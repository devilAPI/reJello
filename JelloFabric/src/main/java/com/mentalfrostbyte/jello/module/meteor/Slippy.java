package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMove;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.player.LocalPlayer;

/**
 * Meteor Client slippy: changes block friction. The real meteor implementation is a one line hook in
 * BlockBehaviour#getFriction; this port also scales ground movement so the setting has a visible effect.
 */
public class Slippy extends Module {
	private final NumberValue friction = addValue(new NumberValue("Friction", 1.0, 0.01, 1.1, 0.01));
	private final ModeValue listMode = addValue(new ModeValue("List Mode", "Blacklist", "Blacklist", "Whitelist"));

	public Slippy() {
		super("Slippy", 0, Category.MOVEMENT);
	}

	@EventTarget
	public void onMove(EventMove event) {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || !p.onGround()) {
			return;
		}
		double f = friction.getValue();
		if (f == 1.0) {
			return;
		}
		event.x *= f;
		event.z *= f;
	}

	/** For a future BlockBehaviour#getFriction mixin. */
	public double getFriction() {
		return isToggled() ? friction.getValue() : 1.0;
	}

	public boolean blacklist() {
		return listMode.is("Blacklist");
	}
}
