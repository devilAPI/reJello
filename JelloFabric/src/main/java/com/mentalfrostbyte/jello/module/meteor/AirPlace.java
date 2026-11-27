package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Meteor Client air-place: places a block where the crosshair points, even into air. */
public class AirPlace extends Module {
	private final BooleanValue customRange = addValue(new BooleanValue("Custom Range", false));
	private final NumberValue range = addValue(new NumberValue("Range", 5.0, 0.0, 100, 0.1));
	private final TimerUtil timer = new TimerUtil();

	public AirPlace() {
		super("Air Place", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gui.screen() != null) {
			return;
		}
		boolean main = placeable(mc.player.getMainHandItem());
		boolean off = placeable(mc.player.getOffhandItem());
		if (!main && !off) {
			return;
		}
		// Only act when the normal crosshair found nothing to click.
		if (mc.hitResult != null && mc.hitResult.getType() != HitResult.Type.MISS) {
			return;
		}
		if (!mc.options.keyUse.isDown() || !timer.hasTimeElapsed(150, true)) {
			return;
		}

		double reach = customRange.getValue() ? range.getValue() : mc.player.blockInteractionRange();
		if (!(mc.getCameraEntity().pick(reach, 0, false) instanceof BlockHitResult hit)) {
			return;
		}
		BlockPos pos = hit.getBlockPos();
		if (!mc.level.getBlockState(pos).canBeReplaced()) {
			return;
		}

		BlockHitResult placement = new BlockHitResult(Vec3.atCenterOf(pos),
			mc.player.getMotionDirection().getOpposite(), pos, false);
		mc.gameMode.useItemOn(mc.player, main ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, placement);
	}

	private static boolean placeable(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		return stack.getItem() instanceof BlockItem
			|| stack.getItem() instanceof SpawnEggItem
			|| stack.getItem() instanceof ArmorStandItem
			|| (stack.getItem() instanceof FireworkRocketItem && !mc.player.isFallFlying());
	}
}
