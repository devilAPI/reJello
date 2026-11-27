package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.player.LocalPlayer;

/** Spins the camera/head. */
public class Derp extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Custom Normal", "Custom Normal", "Custom Silent", "Vanilla", "Verus"));
	private final NumberValue speed = addValue(new NumberValue("Speed", 50, 1, 360, 1));
	private final BooleanValue pitch = addValue(new BooleanValue("Pitch Derp", true));

	public Derp() {
		super("Derp", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null) {
			return;
		}
		float amount = (float) speed.getValue();
		switch (mode.getMode()) {
			case "Custom Silent" -> {
				p.setYHeadRot(p.getYHeadRot() + amount);
				if (pitch.getValue()) {
					p.setXRot(p.getXRot() + amount);
				}
			}
			case "Vanilla" -> {
				p.setYHeadRot(p.getYHeadRot() + amount);
				p.setYBodyRot(amount);
				if (pitch.getValue()) {
					p.setXRot(p.getXRot() + amount);
				}
			}
			case "Verus" -> {
				p.setYHeadRot(p.getYHeadRot() + 37);
				p.setYBodyRot(0);
			}
			default -> {
				p.setYRot(p.getYRot() + amount);
				if (pitch.getValue()) {
					p.setXRot(p.getXRot() + amount);
				}
			}
		}
	}
}
