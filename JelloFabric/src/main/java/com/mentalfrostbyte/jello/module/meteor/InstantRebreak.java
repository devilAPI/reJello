package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Meteor Client instant-rebreak: keeps breaking the last mined block position.
 * Meteor reads the position from a StartBreakingBlock event; we infer it from the crosshair
 * and the attack key because this build has no such event.
 */
public class InstantRebreak extends Module {
	public InstantRebreak() {
		super("Instant Rebreak", 0, Category.PLAYER);
	}

	private final NumberValue delay = addValue(new NumberValue("Delay", 0, 0, 20, 1));
	private final BooleanValue onlyPick = addValue(new BooleanValue("Only Pick", true));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));

	private BlockPos blockPos;
	private Direction direction;
	private int ticks;

	@Override
	public void onEnable() {
		blockPos = null;
		direction = null;
		ticks = 0;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.getConnection() == null) {
			return;
		}

		if ((mc.options.keyAttack.isDown() || mc.gameMode.isDestroying())
			&& mc.hitResult instanceof BlockHitResult hit
			&& !mc.level.getBlockState(hit.getBlockPos()).isAir()) {
			blockPos = hit.getBlockPos();
			direction = hit.getDirection();
		}
		if (blockPos == null || !shouldMine()) {
			return;
		}
		// Only keep re-breaking while the attack input is still active.
		if (!mc.options.keyAttack.isDown() && !mc.gameMode.isDestroying()) {
			return;
		}
		if (ticks++ < delay.getValue()) {
			return;
		}
		ticks = 0;

		if (rotate.getValue()) {
			sendRotation();
		}
		mc.getConnection().send(new ServerboundPlayerActionPacket(
			ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos,
			direction == null ? Direction.UP : direction));
		mc.getConnection().send(new ServerboundPunchPacket());
	}

	private boolean shouldMine() {
		if (mc.level.isOutsideBuildHeight(blockPos)) {
			return false;
		}
		BlockState state = mc.level.getBlockState(blockPos);
		if (state.isAir() || state.getDestroySpeed(mc.level, blockPos) < 0) {
			return false;
		}
		return !onlyPick.getValue() || mc.player.getMainHandItem().is(ItemTags.PICKAXES)
			|| mc.player.getOffhandItem().is(ItemTags.PICKAXES);
	}

	private void sendRotation() {
		Vec3 eye = mc.player.getEyePosition();
		Vec3 target = Vec3.atCenterOf(blockPos);
		double dx = target.x - eye.x;
		double dy = target.y - eye.y;
		double dz = target.z - eye.z;
		float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
		float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
		mc.getConnection().send(new ServerboundMovePlayerPacket.Rot(yaw, pitch,
			mc.player.onGround(), mc.player.horizontalCollision));
	}
}
