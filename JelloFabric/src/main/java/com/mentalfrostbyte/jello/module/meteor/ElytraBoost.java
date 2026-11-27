package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;

/**
 * Meteor Client elytra-boost: spawns a client side firework rocket to boost elytra flight.
 * Meteor hooks item interactions to cancel consumption; without that hook the vanilla rocket still fires.
 */
public class ElytraBoost extends Module {
	private final BooleanValue antiConsume = addValue(new BooleanValue("Anti Consume", true));
	private final NumberValue fireworkDuration = addValue(new NumberValue("Firework Duration", 0, 0, 255, 1));
	private final BooleanValue playSound = addValue(new BooleanValue("Play Sound", true));

	private final TimerUtil timer = new TimerUtil();
	private boolean lastUseDown;

	public ElytraBoost() {
		super("Elytra Boost", 0, Category.MOVEMENT);
	}

	@Override
	public void onDisable() {
		lastUseDown = false;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		boolean useDown = mc.options.keyUse.isDown();
		// Only boost on a fresh right click and never more than four times a second.
		if (useDown && !lastUseDown && mc.player.isFallFlying()
			&& mc.player.getMainHandItem().getItem() == Items.FIREWORK_ROCKET
			&& timer.hasTimeElapsed(250, true)) {
			boost();
		}
		lastUseDown = useDown;
	}

	private void boost() {
		if (mc.player == null || mc.level == null) {
			return;
		}
		ItemStack stack = Items.FIREWORK_ROCKET.getDefaultInstance();
		Fireworks current = stack.get(DataComponents.FIREWORKS);
		stack.set(DataComponents.FIREWORKS, new Fireworks((int) fireworkDuration.getValue(),
			current == null ? java.util.List.of() : current.explosions()));

		FireworkRocketEntity entity = new FireworkRocketEntity(mc.level, stack, mc.player);
		if (playSound.getValue()) {
			mc.level.playSound(mc.player, entity, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.AMBIENT, 3.0f, 1.0f);
		}
		mc.level.addEntity(entity);
	}
}
