package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

/**
 * Meteor Client packet canceller. Meteor offers a full packet-type list; this build uses a small preset selector
 * because the settings system has no string-list setting yet. Outgoing packets are fully hooked; incoming ones are
 * limited to the three types the {@code ClientPacketListenerMixin} already forwards to the event bus.
 */
public class PacketCanceller extends Module {
	private final ModeValue outbound = addValue(new ModeValue("Cancel C2S", "None",
		"None", "Player Move", "Chat", "Keep Alive", "Resource Pack"));
	private final ModeValue inbound = addValue(new ModeValue("Cancel S2C", "None",
		"None", "Explosions", "Entity Motion", "Container Slot"));

	public PacketCanceller() {
		super("Packet Canceller", 0, Category.MISC);
	}

	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (isToggled() && matchesOutbound(event.getPacket())) {
			event.setCancelled(true);
		}
	}

	@EventTarget
	public void onPacketReceived(EventReceivePacket event) {
		if (isToggled() && matchesInbound(event.getPacket())) {
			event.setCancelled(true);
		}
	}

	private boolean matchesOutbound(Packet<?> packet) {
		return switch (outbound.getMode()) {
			case "Player Move" -> packet instanceof ServerboundMovePlayerPacket;
			case "Chat" -> packet instanceof ServerboundChatPacket || packet instanceof ServerboundChatCommandPacket
				|| packet instanceof ServerboundChatCommandSignedPacket || packet instanceof ServerboundChatAckPacket;
			case "Keep Alive" -> packet instanceof ServerboundKeepAlivePacket;
			case "Resource Pack" -> packet instanceof ServerboundResourcePackPacket;
			default -> false;
		};
	}

	private boolean matchesInbound(Packet<?> packet) {
		return switch (inbound.getMode()) {
			case "Explosions" -> packet instanceof ClientboundExplodePacket;
			case "Entity Motion" -> packet instanceof ClientboundSetEntityMotionPacket;
			case "Container Slot" -> packet instanceof ClientboundContainerSetSlotPacket;
			default -> false;
		};
	}
}
