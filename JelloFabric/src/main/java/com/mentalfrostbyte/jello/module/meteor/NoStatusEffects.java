package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Meteor Client no-status-effects: removes selected effects from the local player.
 * Meteor cancels them in LivingEntity.hasEffect; removing the client-side instance gives the
 * same visible/movement behaviour without a mixin.
 */
public class NoStatusEffects extends Module {
	private final BooleanValue levitation = addValue(new BooleanValue("Levitation", true));
	private final BooleanValue jumpBoost = addValue(new BooleanValue("Jump Boost", true));
	private final BooleanValue slowFalling = addValue(new BooleanValue("Slow Falling", true));
	private final BooleanValue dolphinsGrace = addValue(new BooleanValue("Dolphins Grace", true));
	private final BooleanValue blindness = addValue(new BooleanValue("Blindness", false));
	private final BooleanValue nausea = addValue(new BooleanValue("Nausea", false));

	public NoStatusEffects() {
		super("No Status Effects", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		List<MobEffectInstance> effects = new ArrayList<>(mc.player.getActiveEffects());
		for (MobEffectInstance instance : effects) {
			if (blocked(instance.getEffect())) {
				mc.player.removeEffect(instance.getEffect());
			}
		}
	}

	private boolean blocked(Holder<MobEffect> effect) {
		return (levitation.getValue() && effect.is(MobEffects.LEVITATION))
			|| (jumpBoost.getValue() && effect.is(MobEffects.JUMP_BOOST))
			|| (slowFalling.getValue() && effect.is(MobEffects.SLOW_FALLING))
			|| (dolphinsGrace.getValue() && effect.is(MobEffects.DOLPHINS_GRACE))
			|| (blindness.getValue() && effect.is(MobEffects.BLINDNESS))
			|| (nausea.getValue() && effect.is(MobEffects.NAUSEA));
	}
}
