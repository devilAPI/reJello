package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.client.KeyMapping;

/** Toggles the attack key at the configured CPS. */
public class AutoClicker extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	private final NumberValue min = addValue(new NumberValue("MinimumCPS", 6, 1, 20, 1));
	private final NumberValue max = addValue(new NumberValue("MaximumCPS", 8, 1, 20, 1));
	private final BooleanValue clickOnGui = addValue(new BooleanValue("ClickOnGUI", false));
	private final TimerUtil timer = new TimerUtil();
	private boolean down;

	public AutoClicker() {
		super("AutoClicker", 0, Category.COMBAT);
	}

	@Override
	public void onDisable() {
		mc.options.keyAttack.setDown(false);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || KillAura.enabled) {
			return;
		}
		if (!clickOnGui.getValue() && mc.gui.screen() != null) {
			return;
		}
		double cps = min.getValue() + Math.random() * Math.max(0, max.getValue() - min.getValue());
		long interval = (long) (1000.0 / Math.max(1, cps));
		if (!timer.hasTimeElapsed(interval, true)) {
			return;
		}
		down = !down;
		mc.options.keyAttack.setDown(down);
		if (down) {
			KeyMapping.click(com.mojang.blaze3d.platform.InputConstants.Type.MOUSE.getOrCreate(1));
		}
	}
}
