package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMove;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Meteor Client collisions. The block-list collision shapes need a collision-shape mixin; the parts that can be
 * done client-side (unloaded chunk guard, magma block guard) are ported here.
 */
public class Collisions extends Module {
	private final BooleanValue magma = addValue(new BooleanValue("Magma", false));
	private final BooleanValue unloadedChunks = addValue(new BooleanValue("Unloaded Chunks", false));
	private final BooleanValue ignoreBorder = addValue(new BooleanValue("Ignore Border", false));

	public Collisions() {
		super("Collisions", 0, Category.WORLD);
	}

	@EventTarget
	public void onMove(EventMove event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (unloadedChunks.getValue()) {
			int x = (int) Math.floor(mc.player.getX() + event.x) >> 4;
			int z = (int) Math.floor(mc.player.getZ() + event.z) >> 4;
			if (!mc.level.getChunkSource().hasChunk(x, z)) {
				event.x = 0;
				event.z = 0;
				return;
			}
		}
		if (magma.getValue() && !mc.player.isShiftKeyDown()) {
			BlockPos next = BlockPos.containing(mc.player.getX() + event.x, mc.player.getY() + 0.1, mc.player.getZ() + event.z);
			if (mc.level.getBlockState(next).isAir()
				&& mc.level.getBlockState(next.below()).getBlock() == Blocks.MAGMA_BLOCK) {
				event.x = 0;
				event.z = 0;
			}
		}
	}

	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (!isToggled() || mc.player == null || mc.level == null || !unloadedChunks.getValue()) {
			return;
		}
		if (event.getPacket() instanceof ServerboundMovePlayerPacket packet) {
			int x = (int) Math.floor(packet.getX(mc.player.getX())) >> 4;
			int z = (int) Math.floor(packet.getZ(mc.player.getZ())) >> 4;
			if (!mc.level.getChunkSource().hasChunk(x, z)) {
				event.setCancelled(true);
			}
		} else if (event.getPacket() instanceof ServerboundMoveVehiclePacket packet) {
			Vec3 pos = packet.movingTo().position();
			int x = (int) Math.floor(pos.x) >> 4;
			int z = (int) Math.floor(pos.z) >> 4;
			if (!mc.level.getChunkSource().hasChunk(x, z)) {
				event.setCancelled(true);
			}
		}
	}
}
