package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Meteor Client packet mine: breaks the targeted block by sending start/stop action packets. */
public class PacketMine extends Module {
	private final NumberValue delay = addValue(new NumberValue("Delay", 1, 0, 100, 1));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));
	private final BooleanValue autoSwitch = addValue(new BooleanValue("Auto Switch", false));
	private final BooleanValue notOnUse = addValue(new BooleanValue("Not On Use", true));
	private final BooleanValue obscure = addValue(new BooleanValue("Obscure Breaking Progress", false));

	private final List<MinedBlock> blocks = new ArrayList<>();
	private boolean swapped;
	private boolean shouldUpdateSlot;
	private int originalSlot = -1;

	public PacketMine() {
		super("Packet Mine", 0, Category.WORLD);
	}

	@Override
	public void onDisable() {
		blocks.clear();
		restoreSlot();
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}

		if (mc.options.keyAttack.isDown() && mc.hitResult instanceof BlockHitResult hit) {
			BlockPos pos = hit.getBlockPos();
			BlockState state = mc.level.getBlockState(pos);
			if (!state.isAir() && state.getDestroySpeed(mc.level, pos) >= 0 && !isMining(pos)) {
				blocks.add(new MinedBlock(pos.immutable(), hit.getDirection()));
			}
		}

		blocks.removeIf(MinedBlock::shouldRemove);

		if (blocks.isEmpty()) {
			restoreSlot();
			return;
		}

		MinedBlock block = blocks.getFirst();
		block.mine();

		if (block.isReady() && !swapped && autoSwitch.getValue()
			&& (!mc.player.isUsingItem() || !notOnUse.getValue())) {
			int slot = findFastestTool(block.state);
			int selected = mc.player.getInventory().getSelectedSlot();
			if (slot != -1 && slot != selected) {
				if (originalSlot == -1) {
					originalSlot = selected;
				}
				mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
				mc.player.getInventory().setSelectedSlot(slot);
				swapped = true;
				shouldUpdateSlot = true;
			}
		}
	}

	private boolean isMining(BlockPos pos) {
		for (MinedBlock block : blocks) {
			if (block.pos.equals(pos)) {
				return true;
			}
		}
		return false;
	}

	private static int findFastestTool(BlockState state) {
		int best = -1;
		float bestSpeed = 1.0f;
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			float speed = stack.getDestroySpeed(state);
			if (speed > bestSpeed) {
				bestSpeed = speed;
				best = i;
			}
		}
		return best;
	}

	private void restoreSlot() {
		if (shouldUpdateSlot && originalSlot != -1) {
			mc.getConnection().send(new ServerboundSetCarriedItemPacket(originalSlot));
			mc.player.getInventory().setSelectedSlot(originalSlot);
			shouldUpdateSlot = false;
			swapped = false;
			originalSlot = -1;
		}
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

	private final class MinedBlock {
		private final BlockPos pos;
		private final Direction direction;
		private final BlockState state;
		private final BlockState originalState;
		private int timer;
		private int startTime;
		private boolean mining;

		private MinedBlock(BlockPos pos, Direction direction) {
			this.pos = pos;
			this.direction = direction;
			this.originalState = mc.level.getBlockState(pos);
			this.state = originalState;
			this.timer = (int) delay.getValue();
		}

		private boolean shouldRemove() {
			if (mc.level.getBlockState(pos).getBlock() != originalState.getBlock()) {
				return true;
			}
			if (mining && (mc.player.tickCount - startTime) > 50 && progress() > 2.0) {
				return true;
			}
			double range = mc.player.blockInteractionRange();
			return Vec3.atCenterOf(pos).distanceToSqr(mc.player.getEyePosition()) > range * range;
		}

		private double progress() {
			if (!mining) {
				return 0.0;
			}
			float delta = state.getDestroyProgress(mc.player, mc.level, pos);
			return delta * (mc.player.tickCount - startTime + 1);
		}

		private boolean isReady() {
			return progress() >= 1.0;
		}

		private void mine() {
			if (rotate.getValue()) {
				lookAt(pos);
			}
			if (timer > 0) {
				timer--;
				return;
			}
			if (!mining) {
				mc.getConnection().send(new ServerboundPlayerActionPacket(
					ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, pos, direction));
				mc.getConnection().send(new ServerboundPlayerActionPacket(
					ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, direction));
				mining = true;
				startTime = mc.player.tickCount;
			}
			if (obscure.getValue()) {
				mc.getConnection().send(new ServerboundPlayerActionPacket(
					ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, pos, direction));
			}
		}
	}
}
