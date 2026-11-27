package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;

/** Meteor Client fast-climb: faster ladder/vine/powder snow climbing. */
public class FastClimb extends Module {
	private final BooleanValue timerMode = addValue(new BooleanValue("Timer Mode", false));
	private final NumberValue climbSpeed = addValue(new NumberValue("Climb Speed", 0.2872, 0.0, 2.0, 0.01));
	private final NumberValue timer = addValue(new NumberValue("Timer", 1.436, 1.0, 5.0, 0.01));

	/** Set while the timer override should be active (consumed by a Timer hook once available). */
	public double timerOverride = 1;

	public FastClimb() {
		super("Fast Climb", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		timerOverride = 1;
	}

	@Override
	public void onDisable() {
		timerOverride = 1;
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.level == null) {
			return;
		}
		if (timerMode.getValue()) {
			timerOverride = climbing(p) ? timer.getValue() : 1;
		} else {
			timerOverride = 1;
			if (climbing(p)) {
				p.setDeltaMovement(p.getDeltaMovement().x, climbSpeed.getValue(), p.getDeltaMovement().z);
			}
		}
	}

	private static boolean climbing(LocalPlayer p) {
		return (p.horizontalCollision || p.input.keyPresses.jump())
			&& (p.onClimbable() || (p.getInBlockState().getBlock() == Blocks.POWDER_SNOW
			&& PowderSnowBlock.canEntityWalkOnPowderSnow(p)));
	}
}
