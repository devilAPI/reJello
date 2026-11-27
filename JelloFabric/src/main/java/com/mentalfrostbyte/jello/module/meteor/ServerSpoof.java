package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Meteor Client server spoof: rewrites the client brand payload and drops known mod channels.
 * The resource-pack option needs a {@code ClientboundResourcePackPushPacket} receive hook, which is not present in
 * this build, so only the outgoing side is ported.
 */
public class ServerSpoof extends Module {
	private final BooleanValue spoofBrand = addValue(new BooleanValue("Spoof Brand", true));
	private final ModeValue brand = addValue(new ModeValue("Brand", "vanilla", "vanilla", "fabric", "forge", "lunarclient"));
	private final BooleanValue blockChannels = addValue(new BooleanValue("Block Channels", true));

	public ServerSpoof() {
		super("Server Spoof", 0, Category.MISC);
	}

	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (!isToggled() || !(event.getPacket() instanceof ServerboundCustomPayloadPacket packet)) {
			return;
		}
		CustomPacketPayload payload = packet.payload();
		Identifier id = payload.type().id();

		if (blockChannels.getValue()) {
			String channel = id.toString();
			if (channel.contains("fabric") || channel.contains("minecraft:register")) {
				event.setCancelled(true);
				return;
			}
		}

		if (spoofBrand.getValue() && payload instanceof BrandPayload) {
			event.setPacket(new ServerboundCustomPayloadPacket(new BrandPayload(brand.getMode())));
		}
	}
}
