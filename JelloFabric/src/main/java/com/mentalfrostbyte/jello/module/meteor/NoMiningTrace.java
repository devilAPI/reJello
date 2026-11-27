package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Meteor Client no-mining-trace: ignores entities in the crosshair while holding a pickaxe,
 * so blocks behind them can be mined. Implemented by refreshing the client hit result instead
 * of the LocalPlayer.pick mixin Meteor uses.
 */
public class NoMiningTrace extends Module {
	private final BooleanValue onlyPickaxe = addValue(new BooleanValue("Only With Pickaxe", true));

	public NoMiningTrace() {
		super("No Mining Trace", 0, Category.PLAYER);
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (!(mc.hitResult instanceof EntityHitResult hit) || !canWork(hit.getEntity())) {
			return;
		}
		HitResult block = mc.player.pick(mc.player.blockInteractionRange(), event.partialTicks, false);
		if (block.getType() == HitResult.Type.BLOCK) {
			mc.hitResult = block;
		}
	}

	private boolean canWork(Entity entity) {
		if (!onlyPickaxe.getValue()) {
			return true;
		}
		return mc.player.getMainHandItem().is(ItemTags.PICKAXES)
			|| mc.player.getOffhandItem().is(ItemTags.PICKAXES);
	}
}
