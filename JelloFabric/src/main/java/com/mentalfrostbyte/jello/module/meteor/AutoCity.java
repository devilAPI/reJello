package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Meteor Client auto-city: mines the block a trapped player is using as a wall. */
public class AutoCity extends Module {
	private final NumberValue targetRange = addValue(new NumberValue("Target Range", 5.5, 0.0, 7.0, 0.1));
	private final NumberValue breakRange = addValue(new NumberValue("Break Range", 4.5, 0.0, 6.0, 0.1));
	private final ModeValue switchMode = addValue(new ModeValue("Switch Mode", "Normal", "Normal", "Silent"));
	private final BooleanValue support = addValue(new BooleanValue("Support", true));
	private final NumberValue placeRange = addValue(new NumberValue("Place Range", 4.5, 0.0, 6.0, 0.1));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));
	private final BooleanValue swingHand = addValue(new BooleanValue("Swing Hand", false));

	public AutoCity() {
		super("Auto City", 0, Category.COMBAT);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		Player target = MeteorCombatHelper.findTarget(targetRange.getValue(), true);
		if (target == null) {
			return;
		}
		BlockPos city = findCityBlock(target);
		if (city == null) {
			return;
		}

		if (support.getValue() && MeteorCombatHelper.isReplaceable(city.below())
			&& MeteorCombatHelper.withinReach(city.below(), placeRange.getValue())) {
			int blockSlot = MeteorCombatHelper.findBlockSlot(block -> block.defaultDestroyTime() >= 0
				&& block.defaultBlockState().isSolidRender());
			if (blockSlot != -1) {
				MeteorCombatHelper.place(city.below(), blockSlot, rotate.getValue(), true);
			}
		}

		int pickaxe = findPickaxeSlot();
		if (pickaxe == -1 || !MeteorCombatHelper.withinReach(city, breakRange.getValue())) {
			return;
		}

		int previous = mc.player.getInventory().getSelectedSlot();
		MeteorCombatHelper.selectSlot(pickaxe);
		if (rotate.getValue()) {
			float[] rotations = MeteorCombatHelper.rotationsTo(Vec3.atCenterOf(city));
			mc.player.setYRot(rotations[0]);
			mc.player.setXRot(rotations[1]);
		}
		if (mc.gameMode != null && mc.gameMode.destroyBlock(city) && swingHand.getValue()) {
			mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND,
				net.minecraft.world.item.component.SwingAnimation.DEFAULT, false);
		}
		if (switchMode.is("Silent")) {
			MeteorCombatHelper.selectSlot(previous);
		}
		setToggled(false);
	}

	private BlockPos findCityBlock(Player target) {
		if (mc.level == null || mc.player == null) {
			return null;
		}
		BlockPos best = null;
		double bestDistance = Double.MAX_VALUE;
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos pos = target.blockPosition().relative(direction);
			BlockState state = mc.level.getBlockState(pos);
			if (state.isAir() || state.canBeReplaced() || state.getBlock().defaultDestroyTime() < 0) {
				continue;
			}
			if (!mc.level.getBlockState(pos.above()).canBeReplaced()) {
				continue;
			}
			if (!MeteorCombatHelper.withinReach(pos, breakRange.getValue())) {
				continue;
			}
			double distance = Vec3.atCenterOf(pos).distanceToSqr(mc.player.position());
			if (distance < bestDistance) {
				bestDistance = distance;
				best = pos;
			}
		}
		return best;
	}

	private int findPickaxeSlot() {
		if (mc.player == null) {
			return -1;
		}
		for (int i = 0; i < 9; i++) {
			if (mc.player.getInventory().getItem(i).is(ItemTags.PICKAXES)) {
				return i;
			}
		}
		return -1;
	}
}
