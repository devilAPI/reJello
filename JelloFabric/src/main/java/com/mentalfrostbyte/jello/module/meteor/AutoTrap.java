package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Meteor Client auto-trap: boxes a nearby player in with obsidian. */
public class AutoTrap extends Module {
	private final NumberValue placeRange = addValue(new NumberValue("Place Range", 4.0, 0.0, 6.0, 0.1));
	private final NumberValue targetRange = addValue(new NumberValue("Target Range", 3.0, 0.0, 10.0, 0.1));
	private final ModeValue priority = addValue(new ModeValue("Target Priority", "Lowest Health", "Lowest Health", "Lowest Distance"));
	private final ModeValue topMode = addValue(new ModeValue("Top Blocks", "Full", "Full", "Face", "Top", "None"));
	private final ModeValue bottomMode = addValue(new ModeValue("Bottom Blocks", "Platform", "Platform", "Full", "Single", "None"));
	private final NumberValue placeDelay = addValue(new NumberValue("Place Delay", 1, 0, 20, 1));
	private final NumberValue blocksPerTick = addValue(new NumberValue("Blocks Per Tick", 1, 1, 8, 1));
	private final BooleanValue selfToggle = addValue(new BooleanValue("Self Toggle", true));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));

	private final List<BlockPos> positions = new ArrayList<>();
	private Player target;
	private boolean placed;
	private int timer;

	public AutoTrap() {
		super("Auto Trap", 0, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		target = null;
		positions.clear();
		placed = false;
		timer = 0;
	}

	@Override
	public void onDisable() {
		positions.clear();
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (selfToggle.getValue() && placed && positions.isEmpty()) {
			placed = false;
			setToggled(false);
			return;
		}

		int slot = MeteorCombatHelper.findBlockSlot(block -> !block.defaultBlockState().isAir()
			&& block.defaultDestroyTime() >= 0 && block.defaultBlockState().isSolidRender());
		if (slot == -1) {
			return;
		}

		if (target == null || target.isDeadOrDying() || target.isRemoved()
			|| target.distanceTo(mc.player) > targetRange.getValue()) {
			target = MeteorCombatHelper.findTarget(targetRange.getValue(), priority.is("Lowest Health"));
		}
		if (target == null) {
			return;
		}

		fillPositions(target);
		if (positions.isEmpty()) {
			return;
		}
		if (timer++ < (int) placeDelay.getValue()) {
			return;
		}
		timer = 0;

		int count = 0;
		for (BlockPos pos : positions) {
			if (count >= (int) blocksPerTick.getValue()) {
				break;
			}
			if (MeteorCombatHelper.place(pos, slot, rotate.getValue(), true)) {
				placed = true;
				count++;
			}
		}
	}

	private void fillPositions(Player target) {
		positions.clear();
		double epsilon = 1e-5;
		AABB box = target.getBoundingBox();
		Set<BlockPos> corners = new LinkedHashSet<>();
		corners.add(BlockPos.containing(box.minX, box.minY, box.minZ));
		corners.add(BlockPos.containing(box.minX, box.minY, box.maxZ - epsilon));
		corners.add(BlockPos.containing(box.maxX - epsilon, box.minY, box.minZ));
		corners.add(BlockPos.containing(box.maxX - epsilon, box.minY, box.maxZ - epsilon));

		for (BlockPos base : corners) {
			switch (topMode.getMode()) {
				case "Full" -> {
					add(base.above(2));
					addSides(base.above());
				}
				case "Face" -> addSides(base.above());
				case "Top" -> add(base.above(2));
				default -> {
				}
			}

			switch (bottomMode.getMode()) {
				case "Platform" -> {
					add(base.below());
					for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.Plane.HORIZONTAL) {
						add(base.below().relative(direction));
					}
				}
				case "Full" -> {
					add(base.below());
					addSides(base);
				}
				case "Single" -> add(base.below());
				default -> {
				}
			}
		}

		positions.sort(Comparator.comparingDouble(pos ->
			-Vec3.atCenterOf(pos).distanceToSqr(mc.player.position())));
	}

	private void addSides(BlockPos base) {
		for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.Plane.HORIZONTAL) {
			add(base.relative(direction));
		}
	}

	private void add(BlockPos pos) {
		if (positions.contains(pos) || !MeteorCombatHelper.isReplaceable(pos)
			|| !MeteorCombatHelper.withinReach(pos, placeRange.getValue())) {
			return;
		}
		positions.add(pos);
	}
}
