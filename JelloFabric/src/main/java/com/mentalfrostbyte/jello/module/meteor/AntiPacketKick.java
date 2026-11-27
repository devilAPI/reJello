package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/**
 * Meteor Client anti-packet-kick: keeps the connection alive when a packet fails to decode.
 *
 * <p>The actual catch has to happen in the packet decoder ({@code PacketDecoder} / {@code ClientCommonPacketListenerImpl}),
 * which this build does not hook yet, so only the state and the two switches are provided here. A decoder mixin can
 * call {@link #catchExceptions()} and {@link #logExceptions()}.</p>
 */
public class AntiPacketKick extends Module {
	private final BooleanValue catchExceptions = addValue(new BooleanValue("Catch Exceptions", false));
	private final BooleanValue logExceptions = addValue(new BooleanValue("Log Exceptions", true));

	public AntiPacketKick() {
		super("Anti Packet Kick", 0, Category.MISC);
	}

	public boolean catchExceptions() {
		return isToggled() && catchExceptions.getValue();
	}

	public boolean logExceptions() {
		return logExceptions.getValue();
	}
}
