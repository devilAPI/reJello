package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventAttack;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.client.player.LocalPlayer;

/** Critical-hit packet modes. */
public class Criticals extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Packet", "Packet", "Jump", "PacketLegit", "MatrixSemi"));

	public Criticals() {
		super("Criticals", 0, Category.COMBAT);
	}

	@EventTarget
	public void onAttack(EventAttack event) {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.getConnection() == null) {
			return;
		}
		// 26.3 disconnects on a second position packet in the same tick, so the old two-Pos
		// Criticals packet trick cannot be used. Only the client-side ground flag is changed.
		switch (mode.getMode()) {
			case "Jump" -> {
				if (p.onGround()) {
					p.jumpFromGround();
				}
			}
			default -> p.setOnGround(false);
		}
	}
}
