package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;

/**
 * Meteor Client build height: lets you use blocks at the build limit.
 * Minecraft 26.x has an immutable hit result with {@code withDirection}, so no accessor mixin is needed.
 */
public class BuildHeight extends Module {
	public BuildHeight() {
		super("Build Height", 0, Category.WORLD);
	}

	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (!isToggled() || mc.level == null) {
			return;
		}
		if (!(event.getPacket() instanceof ServerboundUseItemOnPacket packet)) {
			return;
		}
		if (packet.hitResult().getLocation().y >= mc.level.getHeight()
			&& packet.hitResult().getDirection() == Direction.UP) {
			event.setPacket(new ServerboundUseItemOnPacket(
				packet.hand(),
				packet.hitResult().withDirection(Direction.DOWN),
				packet.sequence()));
		}
	}
}
