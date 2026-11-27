package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.player.LocalPlayer;

/** Jump boost with the Reborn modes. */
public class HighJump extends Module {
	public static boolean enabled;
	private final ModeValue mode = addValue(new ModeValue("Mode", "Vanilla", "Vanilla", "Watchdog A"));
	private final BooleanValue silent = addValue(new BooleanValue("Silent Fly", true));
	private final NumberValue height = addValue(new NumberValue("Height", 3.0, 0.5, 5.0, 0.5));

	public HighJump() {
		super("HighJump", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		enabled = true;
	}

	@Override
	public void onDisable() {
		enabled = false;
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || !p.onGround() || !mc.options.keyJump.isDown()) {
			return;
		}
		double boost = 0.42 + height.getValue() * 0.12;
		p.setDeltaMovement(p.getDeltaMovement().x, boost, p.getDeltaMovement().z);
		p.jumpFromGround();
	}
}
