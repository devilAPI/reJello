package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** Teleports to the looked-at block on right click. */
public class ClickTP extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	public ClickTP() {
		super("ClickTP", 0, Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || !mc.options.keyUse.isDown()) {
			return;
		}
		HitResult hit = mc.hitResult;
		if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
			return;
		}
		BlockPos pos = blockHit.getBlockPos();
		mc.player.setPos(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5);
		mc.player.setDeltaMovement(0, 0, 0);
		mc.options.keyUse.setDown(false);
	}
}
