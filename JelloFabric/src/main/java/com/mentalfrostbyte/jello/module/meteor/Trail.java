package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventTick;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

/** Meteor Client Trail: spawns a particle trail behind the player. */
public class Trail extends Module {
	private final ModeValue particle = addValue(new ModeValue("Particle", "Campfire Smoke",
		"Campfire Smoke", "Obsidian Tear", "Flame", "Heart", "Cloud", "End Rod"));
	private final BooleanValue pause = addValue(new BooleanValue("Pause When Stationary", true));

	public Trail() {
		super("Trail", 0, Category.RENDER);
	}

	@EventTarget
	public void onTick(EventTick event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (pause.getValue()
			&& mc.player.getX() == mc.player.xOld
			&& mc.player.getY() == mc.player.yOld
			&& mc.player.getZ() == mc.player.zOld) {
			return;
		}

		SimpleParticleType type = switch (particle.getMode()) {
			case "Obsidian Tear" -> ParticleTypes.DRIPPING_OBSIDIAN_TEAR;
			case "Flame" -> ParticleTypes.FLAME;
			case "Heart" -> ParticleTypes.HEART;
			case "Cloud" -> ParticleTypes.CLOUD;
			case "End Rod" -> ParticleTypes.END_ROD;
			default -> ParticleTypes.CAMPFIRE_COSY_SMOKE;
		};

		mc.level.addParticle(type, mc.player.getX(), mc.player.getY() + 0.1, mc.player.getZ(), 0, 0, 0);
	}
}
