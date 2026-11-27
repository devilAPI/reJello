package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.util.PlayerUtil;
import net.minecraft.world.effect.MobEffect;

/**
 * Meteor Client potion-saver: stops effect duration ticking while standing still.
 * Meteor cancels MobEffectInstance.tickDownDuration in a mixin; this build has no such hook, so
 * {@link #shouldFreeze(MobEffect)} is provided for a future mixin and the module itself only
 * holds the settings.
 */
public class PotionSaver extends Module {
	private final BooleanValue onlyWhenStationary = addValue(new BooleanValue("Only When Stationary", false));

	public PotionSaver() {
		super("Potion Saver", 0, Category.PLAYER);
	}

	/** For the MobEffectInstance mixin: true when this effect should not count down. */
	public boolean shouldFreeze(MobEffect effect) {
		return isToggled() && mc.player != null
			&& (!onlyWhenStationary.getValue() || !PlayerUtil.isMoving())
			&& !mc.player.getActiveEffects().isEmpty();
	}
}
