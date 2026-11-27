package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Meteor Client multitask: use items and attack at the same time.
 * Meteor patches three vanilla checks; without mixins this re-issues the suppressed attack and
 * block use from the tick loop, leaving only the exact keybind path unpatched.
 */
public class Multitask extends Module {
	private final BooleanValue attackingEntities = addValue(new BooleanValue("Attacking Entities", true));
	private final TimerUtil useTimer = new TimerUtil();

	public Multitask() {
		super("Multitask", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gui.screen() != null) {
			return;
		}

		if (attackingEntities.getValue() && mc.player.isUsingItem() && mc.options.keyAttack.isDown()
			&& mc.player.getAttackStrengthScale(0.5F) >= 1.0F
			&& mc.hitResult instanceof EntityHitResult hit
			&& hit.getEntity() != mc.player && hit.getEntity().isAlive()) {
			mc.gameMode.attack(mc.player, hit.getEntity());
			mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
		}

		// Vanilla refuses to start a right-click while a block is being mined.
		if (mc.gameMode.isDestroying() && mc.options.keyUse.isDown() && !mc.player.isUsingItem()
			&& mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK
			&& useTimer.hasTimeElapsed(200, true)) {
			mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, (BlockHitResult) mc.hitResult);
		}
	}
}
