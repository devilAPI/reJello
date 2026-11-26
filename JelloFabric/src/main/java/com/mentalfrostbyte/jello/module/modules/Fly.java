package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.player.LocalPlayer;

/** Creative-style flight, like the original. */
public class Fly extends Module {
	public Fly() {
		super("Fly", 0, Category.MOVEMENT);
	}

	@Override
	public void onDisable() {
		if (mc.player != null) {
			LocalPlayer p = mc.player;
			p.getAbilities().flying = false;
			if (!p.isCreative() && !p.isSpectator()) {
				p.getAbilities().mayfly = false;
			}
			p.onUpdateAbilities();
		}
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		LocalPlayer p = mc.player;
		p.getAbilities().mayfly = true;
		p.getAbilities().flying = true;
		p.onUpdateAbilities();
	}
}
