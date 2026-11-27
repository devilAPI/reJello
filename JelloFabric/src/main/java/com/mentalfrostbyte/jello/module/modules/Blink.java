package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

/** Holds movement packets and flushes them on disable or on a pulse. */
public class Blink extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	private final List<Packet<?>> packets = new ArrayList<>();
	private final TimerUtil timer = new TimerUtil();
	private final BooleanValue pulse = addValue(new BooleanValue("Pulse", false));
	private final NumberValue pulseDelay = addValue(new NumberValue("PulseDelay", 300, 10, 1000, 1));

	public Blink() {
		super("Blink", 0, Category.PLAYER);
	}

	@Override
	public void onEnable() {
		packets.clear();
		timer.reset();
	}

	@Override
	public void onDisable() {
		flush();
	}

	@Override
	public void onUpdate() {
		if (isToggled() && pulse.getValue() && timer.hasTimeElapsed((long) pulseDelay.getValue(), true)) {
			flush();
		}
	}

	private void flush() {
		if (mc.getConnection() != null) {
			for (Packet<?> packet : packets) {
				mc.getConnection().send(packet);
			}
		}
		packets.clear();
	}

	@EventTarget
	public void onPacket(EventPacketSent event) {
		if (event.getPacket() instanceof ServerboundMovePlayerPacket) {
			packets.add(event.getPacket());
			event.setCancelled(true);
		}
	}
}
