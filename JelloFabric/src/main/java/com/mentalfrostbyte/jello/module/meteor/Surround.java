package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/** Meteor Client surround: places obsidian around the local player's feet. */
public class Surround extends Module {
	private final NumberValue delay = addValue(new NumberValue("Delay", 0, 0, 20, 1));
	private final NumberValue blocksPerTick = addValue(new NumberValue("Blocks Per Tick", 1, 1, 8, 1));
	private final ModeValue center = addValue(new ModeValue("Center", "Incomplete", "Never", "On Activate", "Incomplete", "Always"));
	private final BooleanValue doubleHeight = addValue(new BooleanValue("Double Height", false));
	private final BooleanValue onlyOnGround = addValue(new BooleanValue("Only On Ground", true));
	private final BooleanValue airPlace = addValue(new BooleanValue("Air Place", true));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));
	private final BooleanValue swing = addValue(new BooleanValue("Swing", true));
	private final BooleanValue toggleOnYChange = addValue(new BooleanValue("Toggle On Y Change", true));
	private final BooleanValue toggleOnComplete = addValue(new BooleanValue("Toggle On Complete", false));
	private final BooleanValue toggleOnDeath = addValue(new BooleanValue("Toggle On Death", true));

	private int timer;

	public Surround() {
		super("Surround", 0, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		timer = (int) delay.getValue();
		if (center.is("On Activate") || center.is("Always")) {
			MeteorCombatHelper.centerPlayer();
		}
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (timer++ < (int) delay.getValue()) {
			return;
		}
		if (toggleOnYChange.getValue() && mc.player.yOld != mc.player.getY()) {
			setToggled(false);
			return;
		}
		if (toggleOnDeath.getValue() && mc.player.isDeadOrDying()) {
			setToggled(false);
			return;
		}
		if (onlyOnGround.getValue() && !mc.player.onGround()) {
			return;
		}

		int slot = MeteorCombatHelper.findBlockSlot(block -> block.defaultDestroyTime() >= 0
			&& block.defaultBlockState().isSolidRender());
		if (slot == -1) {
			return;
		}
		if (center.is("Always")) {
			MeteorCombatHelper.centerPlayer();
		}

		BlockPos base = mc.player.blockPosition();
		int count = 0;
		boolean complete = true;

		// Determine completion before placing, otherwise the blocks-per-tick break can leave it stale.
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos pos = base.relative(direction);
			if (MeteorCombatHelper.isReplaceable(pos)) {
				complete = false;
			}
			if (!airPlace.getValue() && isAirPlace(pos) && MeteorCombatHelper.isReplaceable(pos.below())) {
				complete = false;
			}
			if (doubleHeight.getValue() && MeteorCombatHelper.isReplaceable(pos.above())) {
				complete = false;
			}
		}

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos pos = base.relative(direction);
			if (!airPlace.getValue() && isAirPlace(pos) && MeteorCombatHelper.isReplaceable(pos)) {
				if (count < (int) blocksPerTick.getValue()
					&& MeteorCombatHelper.place(pos.below(), slot, rotate.getValue(), swing.getValue())) {
					count++;
				}
			}

			if (count < (int) blocksPerTick.getValue()
				&& MeteorCombatHelper.place(pos, slot, rotate.getValue(), swing.getValue())) {
				count++;
			}
			if (count >= (int) blocksPerTick.getValue()) {
				break;
			}
		}

		if (doubleHeight.getValue() && complete) {
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				BlockPos pos = base.relative(direction).above();
				if (count < (int) blocksPerTick.getValue()
					&& MeteorCombatHelper.place(pos, slot, rotate.getValue(), swing.getValue())) {
					count++;
				}
				if (count >= (int) blocksPerTick.getValue()) {
					break;
				}
			}
		}

		timer = 0;

		if (complete && toggleOnComplete.getValue()) {
			setToggled(false);
			return;
		}
		if (!complete && center.is("Incomplete")) {
			MeteorCombatHelper.centerPlayer();
		}
	}

	private boolean isAirPlace(BlockPos pos) {
		if (mc.level == null) {
			return false;
		}
		for (Direction direction : Direction.values()) {
			BlockState state = mc.level.getBlockState(pos.relative(direction));
			if (!state.canBeReplaced()) {
				return false;
			}
		}
		return true;
	}
}
