package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.player.LocalPlayer;

/** Creative-style flight with the Reborn setting set. */
public class Fly extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	private final ModeValue mode = addValue(new ModeValue("Mode", "Blocksmc", "Vanilla", "Verus", "Blocksmc", "Matrix", "MatrixNew",
		"MatrixVertical", "BuzzShort", "ZoneCraft", "AAC4", "Vulcan", "NCP", "BlocksMC UnSafe", "ZoneCraft2", "Vulcan2.6.4"));
	private final NumberValue flagTimes = addValue(new NumberValue("FlagTimes", 1, 1, 4, 1));
	private final BooleanValue fakeDamage = addValue(new BooleanValue("FakeDamage", true));
	private final BooleanValue verusDamage = addValue(new BooleanValue("VerusDamage", false));
	private final NumberValue matrixBoost = addValue(new NumberValue("MatrixBoost", 1, 1, 3, 0.1));
	private final NumberValue matrixVerBoost = addValue(new NumberValue("MatrixVerBoost", 1, 0.1, 8, 0.1));
	private final NumberValue vulcanTimer = addValue(new NumberValue("Vulcan Timer", 2.0, 1.0, 3.0, 0.2));
	private final BooleanValue vanillaKickBypass = addValue(new BooleanValue("Vanilla Kick Bypass", true));
	private final BooleanValue autoDisable = addValue(new BooleanValue("AutoDisable", false));

	public Fly() {
		super("Fly", 0, Category.MOVEMENT);
	}

	@Override
	public void onDisable() {
		LocalPlayer p = mc.player;
		if (p != null) {
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
		p.getAbilities().setFlyingSpeed((float) (0.05 + matrixBoost.getValue() * 0.015));
		p.onUpdateAbilities();
	}
}
