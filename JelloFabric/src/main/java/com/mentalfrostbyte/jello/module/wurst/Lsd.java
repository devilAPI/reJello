package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Wurst LSD: psychedelic screen distortion.
 *
 * <p>Wurst registers a custom {@code wurst:lsd} post effect through
 * {@code LocalPlayer.getActivePostEffects()}. Jello ships no post-effect shader resource, so the
 * safe subset applies vanilla nausea while enabled. Full port needs the {@code wurst:lsd} post
 * effect shader/post-chain registration plus a render hook that reads the active effect list.</p>
 */
public class Lsd extends Module {
	public Lsd() {
		super("LSD", 0, Category.MISC);
	}

	@Override
	public void onEnable() {
		if (mc.player == null) {
			return;
		}
		mc.player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 20 * 60 * 60, 0, true, false, false));
	}

	@Override
	public void onDisable() {
		if (mc.player != null) {
			mc.player.removeEffect(MobEffects.NAUSEA);
		}
	}
}
