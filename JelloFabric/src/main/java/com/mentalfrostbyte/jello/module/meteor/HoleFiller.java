package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Meteor Client hole-filler: fills safe holes around enemy players. */
public class HoleFiller extends Module {
	private final NumberValue searchRadius = addValue(new NumberValue("Search Radius", 5, 0, 8, 1));
	private final NumberValue placeRange = addValue(new NumberValue("Place Range", 4.5, 0.0, 6.0, 0.1));
	private final BooleanValue doubles = addValue(new BooleanValue("Doubles", true));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", false));
	private final NumberValue placeDelay = addValue(new NumberValue("Place Delay", 1, 0, 20, 1));
	private final NumberValue blocksPerTick = addValue(new NumberValue("Blocks Per Tick", 3, 1, 8, 1));
	private final BooleanValue smart = addValue(new BooleanValue("Smart", true));
	private final BooleanValue predictMovement = addValue(new BooleanValue("Predict Movement", true));
	private final NumberValue ticksToPredict = addValue(new NumberValue("Ticks To Predict", 10, 1, 30, 1));
	private final BooleanValue ignoreSafe = addValue(new BooleanValue("Ignore Safe", true));
	private final BooleanValue onlyMoving = addValue(new BooleanValue("Only Moving", true));
	private final NumberValue targetRange = addValue(new NumberValue("Target Range", 7.0, 0.0, 10.0, 0.1));
	private final NumberValue feetRange = addValue(new NumberValue("Feet Range", 1.5, 0.0, 4.0, 0.1));
	private final BooleanValue swing = addValue(new BooleanValue("Swing", true));

	private final List<BlockPos> holes = new ArrayList<>();
	private int timer;

	public HoleFiller() {
		super("Hole Filler", 0, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		timer = 0;
		holes.clear();
	}

	@Override
	public void onDisable() {
		holes.clear();
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		int slot = MeteorCombatHelper.findBlockSlot(block -> block.defaultDestroyTime() >= 0
			&& block.defaultBlockState().isSolidRender());
		if (slot == -1) {
			return;
		}

		List<Player> targets = smart.getValue() ? findTargets() : List.of();
		holes.clear();
		int radius = (int) searchRadius.getValue();
		BlockPos center = mc.player.blockPosition();
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				for (int y = -2; y <= 2; y++) {
					BlockPos pos = center.offset(x, y, z);
					if (validHole(pos, targets)) {
						holes.add(pos);
					}
				}
			}
		}
		if (holes.isEmpty()) {
			return;
		}
		holes.sort(Comparator.comparingDouble(pos -> Vec3.atCenterOf(pos).distanceToSqr(mc.player.position())));

		if (timer > 0) {
			timer--;
			return;
		}
		int count = 0;
		for (BlockPos hole : holes) {
			if (count >= (int) blocksPerTick.getValue()) {
				break;
			}
			if (MeteorCombatHelper.place(hole, slot, rotate.getValue(), swing.getValue())) {
				count++;
			}
		}
		if (count > 0) {
			timer = (int) placeDelay.getValue();
		}
	}

	private boolean validHole(BlockPos pos, List<Player> targets) {
		if (mc.level == null || !mc.level.getBlockState(pos).canBeReplaced()
			|| !mc.level.getBlockState(pos.above()).canBeReplaced()
			|| !MeteorCombatHelper.withinReach(pos, placeRange.getValue())
			|| !MeteorCombatHelper.isHard(mc.level.getBlockState(pos.below()).getBlock())) {
			return false;
		}

		Direction open = null;
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos side = pos.relative(direction);
			BlockState state = mc.level.getBlockState(side);
			if (MeteorCombatHelper.isHard(state.getBlock())) {
				continue;
			}
			if (open != null) {
				return false;
			}
			open = direction;
		}
		if (open != null) {
			if (!doubles.getValue()) {
				return false;
			}
			BlockPos other = pos.relative(open);
			if (!mc.level.getBlockState(other).canBeReplaced()
				|| !MeteorCombatHelper.isHard(mc.level.getBlockState(other.below()).getBlock())) {
				return false;
			}
		}

		if (!smart.getValue()) {
			return true;
		}
		for (Player target : targets) {
			if (target.getY() > pos.getY() && isCloseToHole(target, pos)) {
				return true;
			}
		}
		return false;
	}

	private List<Player> findTargets() {
		List<Player> targets = new ArrayList<>();
		if (mc.level == null || mc.player == null) {
			return targets;
		}
		for (Player player : mc.level.players()) {
			if (player == mc.player || player.isDeadOrDying() || player.isCreative() || player.isSpectator()
				|| player.distanceToSqr(mc.player) > targetRange.getValue() * targetRange.getValue()) {
				continue;
			}
			if (ignoreSafe.getValue() && isSurrounded(player)) {
				continue;
			}
			if (onlyMoving.getValue() && !isMoving(player)) {
				continue;
			}
			targets.add(player);
		}
		return targets;
	}

	private boolean isMoving(Player player) {
		return player.getX() != player.xo || player.getY() != player.yo || player.getZ() != player.zo;
	}

	private boolean isSurrounded(Player target) {
		if (mc.level == null) {
			return false;
		}
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			if (!MeteorCombatHelper.isHard(mc.level.getBlockState(target.blockPosition().relative(direction)).getBlock())) {
				return false;
			}
		}
		return true;
	}

	private boolean isCloseToHole(Player target, BlockPos hole) {
		Vec3 pos = target.position();
		if (predictMovement.getValue()) {
			double dx = target.getX() - target.xo;
			double dy = target.getY() - target.yo;
			double dz = target.getZ() - target.zo;
			pos = pos.add(dx * ticksToPredict.getValue(), dy * ticksToPredict.getValue(), dz * ticksToPredict.getValue());
		}
		double dx = pos.x - (hole.getX() + 0.5);
		double dy = pos.y - (hole.getY() + 1.0);
		double dz = pos.z - (hole.getZ() + 0.5);
		return Math.sqrt(dx * dx + dy * dy + dz * dz) < feetRange.getValue();
	}
}
