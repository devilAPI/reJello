package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Meteor Client no ghost blocks. Meteor cancels its own client-side prediction through two mixins; without
 * those, this version snapshots the affected blocks and restores the client state right after the prediction.
 */
public class NoGhostBlocks extends Module {
	private final BooleanValue breaking = addValue(new BooleanValue("Breaking", true));
	private final BooleanValue placing = addValue(new BooleanValue("Placing", true));

	private final Map<BlockPos, BlockState> breakSnapshots = new HashMap<>();
	private final Map<BlockPos, BlockState> placeSnapshots = new HashMap<>();

	public NoGhostBlocks() {
		super("No Ghost Blocks", 0, Category.WORLD);
	}

	@Override
	public void onDisable() {
		breakSnapshots.clear();
		placeSnapshots.clear();
	}

	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}

		if (breaking.getValue() && !mc.isLocalServer()
			&& event.getPacket() instanceof ServerboundPlayerActionPacket packet) {
			BlockPos pos = packet.getPos();
			if (packet.getAction() == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
				breakSnapshots.putIfAbsent(pos.immutable(), mc.level.getBlockState(pos));
			} else if (packet.getAction() == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
				BlockState before = breakSnapshots.remove(pos);
				if (before != null && mc.level.getBlockState(pos).isAir()) {
					mc.level.setBlock(pos, before, Block.UPDATE_ALL);
				}
			} else if (packet.getAction() == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK) {
				breakSnapshots.remove(pos);
			}
		}

		if (placing.getValue() && event.getPacket() instanceof ServerboundUseItemOnPacket packet
			&& mc.player.getMainHandItem().getItem() instanceof BlockItem item) {
			BlockPos support = packet.hitResult().getBlockPos();
			BlockPos clicked = support.relative(packet.hitResult().getDirection());
			restorePlacement(support, item.getBlock());
			restorePlacement(clicked, item.getBlock());
		}
	}

	private void restorePlacement(BlockPos pos, Block block) {
		BlockState before = placeSnapshots.get(pos);
		if (before == null) {
			return;
		}
		BlockState now = mc.level.getBlockState(pos);
		if (now.getBlock() == block) {
			mc.level.setBlock(pos, before, Block.UPDATE_ALL);
		}
	}

	@Override
	public void onUpdate() {
		placeSnapshots.clear();
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (!(mc.player.getMainHandItem().getItem() instanceof BlockItem) || !placing.getValue()) {
			return;
		}
		if (!(mc.hitResult instanceof BlockHitResult hit)) {
			return;
		}

		BlockPos pos = hit.getBlockPos();
		placeSnapshots.put(pos, mc.level.getBlockState(pos));
		BlockPos side = pos.relative(hit.getDirection());
		if (!side.equals(pos)) {
			placeSnapshots.put(side, mc.level.getBlockState(side));
		}
	}
}
