package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

/** Meteor Client self-trap: places blocks above the local player's head. */
public class SelfTrap extends Module {
	private final ModeValue topMode = addValue(new ModeValue("Top Mode", "Top", "Top", "Full", "Anti Face Place", "None"));
	private final ModeValue bottomMode = addValue(new ModeValue("Bottom Mode", "None", "Single", "None"));
	private final NumberValue placeDelay = addValue(new NumberValue("Place Delay", 1, 0, 20, 1));
	private final BooleanValue center = addValue(new BooleanValue("Center", true));
	private final BooleanValue turnOff = addValue(new BooleanValue("Turn Off", true));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));

	private final List<BlockPos> positions = new ArrayList<>();
	private boolean placed;
	private int delay;

	public SelfTrap() {
		super("Self Trap", 0, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		positions.clear();
		placed = false;
		delay = 0;
		if (center.getValue()) {
			MeteorCombatHelper.centerPlayer();
		}
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
		int slot = MeteorCombatHelper.findBlockSlot(block -> block.defaultDestroyTime() >= 0
			&& block.defaultBlockState().isSolidRender());
		if (slot == -1) {
			if (turnOff.getValue()) {
				setToggled(false);
			}
			return;
		}

		findPositions();
		if (turnOff.getValue() && placed && positions.isEmpty()) {
			setToggled(false);
			return;
		}
		if (positions.isEmpty() || delay++ < (int) placeDelay.getValue()) {
			return;
		}
		delay = 0;

		BlockPos pos = positions.get(positions.size() - 1);
		if (MeteorCombatHelper.place(pos, slot, rotate.getValue(), true)) {
			positions.remove(pos);
			placed = true;
		}
	}

	private void findPositions() {
		positions.clear();
		BlockPos base = mc.player.blockPosition();

		switch (topMode.getMode()) {
			case "Full" -> {
				add(base.above(2));
				for (Direction direction : Direction.Plane.HORIZONTAL) {
					add(base.above().relative(direction));
				}
			}
			case "Anti Face Place" -> {
				for (Direction direction : Direction.Plane.HORIZONTAL) {
					add(base.above().relative(direction));
				}
			}
			case "Top" -> add(base.above(2));
			default -> {
			}
		}

		if (bottomMode.is("Single")) {
			add(base.below());
		}
	}

	private void add(BlockPos pos) {
		if (mc.level == null || positions.contains(pos) || !mc.level.getBlockState(pos).canBeReplaced()) {
			return;
		}
		BlockState state = mc.level.getBlockState(pos);
		if (!mc.level.isUnobstructed(state.getBlock().defaultBlockState(), pos, CollisionContext.empty())) {
			return;
		}
		positions.add(pos);
	}
}
