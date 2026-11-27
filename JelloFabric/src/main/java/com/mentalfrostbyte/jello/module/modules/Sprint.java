package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;

/** Sigma Reborn Sprint: keeps the player sprinting according to the selected mode. */
public class Sprint extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	private final ModeValue sprintMode = addValue(new ModeValue("OmniSprint", "Old", "Old", "New", "NewOmni"));
	public static boolean scb;

	public Sprint() {
		super("Sprint", 0, Category.MOVEMENT);
	}

	@Override
	public void onDisable() {
		if (mc.player != null && !mc.player.isSprinting()) {
			mc.options.keySprint.setDown(false);
		}
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null) {
			return;
		}
		switch (sprintMode.getMode()) {
			case "NewOmni" -> p.setSprinting(true);
			case "New" -> {
				if (!p.isUsingItem() && !p.isDeadOrDying() && !p.isShiftKeyDown()) {
					mc.options.keySprint.setDown(true);
				}
			}
			default -> {
				if (!p.isUsingItem() && p.getDeltaMovement().horizontalDistanceSqr() > 0.001 && !scb) {
					p.setSprinting(true);
				}
			}
		}
	}
}
