package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Packet Logger). Behaviour port pending. */
public class PacketLogger extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("show-timestamp", true));
	private final BooleanValue value2 = addValue(new BooleanValue("show-packet-data", false));
	private final BooleanValue value3 = addValue(new BooleanValue("show-count", true));
	private final BooleanValue value4 = addValue(new BooleanValue("show-summary", true));
	private final BooleanValue value5 = addValue(new BooleanValue("log-to-chat", true));
	private final BooleanValue value6 = addValue(new BooleanValue("log-to-file", false));
	private final NumberValue value7 = addValue(new NumberValue("flush-interval", 1, 1, 100, 1));
	private final NumberValue value8 = addValue(new NumberValue("max-file-size-mb", 10, 1, 100, 1));
	private final NumberValue value9 = addValue(new NumberValue("max-total-logs-mb", 50, 1, 100, 1));

	public PacketLogger() {
		super("Packet Logger", 0, Category.MISC);
	}
}
