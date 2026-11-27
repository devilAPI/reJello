package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Meteor Client vein miner: when a block is attacked, queues every connected block of the same item type
 * and keeps mining them.
 */
public class VeinMiner extends Module {
	private static final Vec3i[] NEIGHBOURS = {
		new Vec3i(1, -1, 1), new Vec3i(0, -1, 1), new Vec3i(-1, -1, 1),
		new Vec3i(1, -1, 0), new Vec3i(0, -1, 0), new Vec3i(-1, -1, 0),
		new Vec3i(1, -1, -1), new Vec3i(0, -1, -1), new Vec3i(-1, -1, -1),
		new Vec3i(1, 0, 1), new Vec3i(0, 0, 1), new Vec3i(-1, 0, 1),
		new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0),
		new Vec3i(1, 0, -1), new Vec3i(0, 0, -1), new Vec3i(-1, 0, -1),
		new Vec3i(1, 1, 1), new Vec3i(0, 1, 1), new Vec3i(-1, 1, 1),
		new Vec3i(1, 1, 0), new Vec3i(0, 1, 0), new Vec3i(-1, 1, 0),
		new Vec3i(1, 1, -1), new Vec3i(0, 1, -1), new Vec3i(-1, 1, -1)
	};

	private final ModeValue mode = addValue(new ModeValue("Mode", "Blacklist", "Blacklist", "Whitelist"));
	private final NumberValue depth = addValue(new NumberValue("Depth", 3, 1, 15, 1));
	private final NumberValue delay = addValue(new NumberValue("Delay", 0, 0, 20, 1));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));
	private final BooleanValue swingHand = addValue(new BooleanValue("Swing Hand", true));

	private final Deque<BlockPos> queue = new ArrayDeque<>();
	private final Set<BlockPos> found = new HashSet<>();
	private BlockPos lastStart;
	private BlockPos lastMined;
	private int tick;

	public VeinMiner() {
		super("Vein Miner", 0, Category.WORLD);
	}

	@Override
	public void onDisable() {
		queue.clear();
		found.clear();
		lastStart = null;
		lastMined = null;
		tick = 0;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}

		if (mc.options.keyAttack.isDown() && mc.hitResult instanceof BlockHitResult hit) {
			BlockPos pos = hit.getBlockPos();
			if (!pos.equals(lastStart)) {
				lastStart = pos;
				schedule(hit.getBlockPos(), hit.getDirection());
			}
		} else if (!mc.options.keyAttack.isDown()) {
			lastStart = null;
		}

		queue.removeIf(pos -> !isStillTarget(pos));
		if (queue.isEmpty()) {
			lastMined = null;
			tick = 0;
			return;
		}

		if (tick < (int) delay.getValue()) {
			tick++;
			return;
		}
		tick = 0;

		BlockPos target = queue.peekFirst();
		if (target == null) {
			return;
		}
		if (!target.equals(lastMined)) {
			lastMined = target;
			if (swingHand.getValue()) {
				mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
			}
		}
		if (rotate.getValue()) {
			lookAt(target);
		}
		mc.gameMode.continueDestroyBlock(target, Direction.UP);
	}

	private void schedule(BlockPos start, Direction direction) {
		BlockState state = mc.level.getBlockState(start);
		if (state.getDestroySpeed(mc.level, start) < 0 || !isSelected(state.getBlock())) {
			return;
		}

		queue.clear();
		found.clear();
		Item item = state.getBlock().asItem();
		scan(item, start, direction, (int) depth.getValue());
	}

	private void scan(Item item, BlockPos pos, Direction direction, int remaining) {
		if (remaining < 0 || !found.add(pos) || !withinReach(pos)) {
			return;
		}
		if (mc.level.getBlockState(pos).getBlock().asItem() != item) {
			return;
		}

		queue.add(pos.immutable());
		for (Vec3i offset : NEIGHBOURS) {
			scan(item, pos.offset(offset), direction, remaining - 1);
		}
	}

	private boolean isStillTarget(BlockPos pos) {
		BlockState state = mc.level.getBlockState(pos);
		return !state.isAir() && state.getDestroySpeed(mc.level, pos) >= 0 && withinReach(pos);
	}

	private boolean isSelected(Block block) {
		if (mode.is("Whitelist")) {
			String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
			return path.contains("_ore") || path.equals("ancient_debris");
		}
		return block != Blocks.STONE && block != Blocks.DIRT && block != Blocks.GRASS_BLOCK;
	}

	private boolean withinReach(BlockPos pos) {
		double range = mc.player.blockInteractionRange();
		return Vec3.atCenterOf(pos).distanceToSqr(mc.player.getEyePosition()) <= range * range;
	}

	private void lookAt(BlockPos pos) {
		Vec3 eye = mc.player.getEyePosition();
		Vec3 target = Vec3.atCenterOf(pos);
		double dx = target.x - eye.x;
		double dy = target.y - eye.y;
		double dz = target.z - eye.z;
		mc.player.setYRot((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
		mc.player.setXRot((float) (-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)))));
	}
}
