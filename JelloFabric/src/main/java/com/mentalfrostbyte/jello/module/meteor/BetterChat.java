package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Better Chat). Behaviour port pending. */
public class BetterChat extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("annoy", false));
	private final BooleanValue value2 = addValue(new BooleanValue("fancy-chat", false));
	private final BooleanValue value3 = addValue(new BooleanValue("timestamps", false));
	private final BooleanValue value4 = addValue(new BooleanValue("show-seconds", false));
	private final BooleanValue value5 = addValue(new BooleanValue("player-heads", true));
	private final BooleanValue value6 = addValue(new BooleanValue("coords-protection", true));
	private final BooleanValue value7 = addValue(new BooleanValue("keep-history", true));
	private final BooleanValue value8 = addValue(new BooleanValue("anti-spam", true));
	private final NumberValue value9 = addValue(new NumberValue("depth", 20, 1, 100, 1));
	private final BooleanValue value10 = addValue(new BooleanValue("anti-clear", true));
	private final BooleanValue value11 = addValue(new BooleanValue("filter-regex", false));
	private final BooleanValue value12 = addValue(new BooleanValue("infinite-chat-box", true));
	private final BooleanValue value13 = addValue(new BooleanValue("longer-chat-history", true));
	private final NumberValue value14 = addValue(new NumberValue("extra-lines", 1000, 0, 1000, 1));
	private final BooleanValue value15 = addValue(new BooleanValue("prefix", false));
	private final BooleanValue value16 = addValue(new BooleanValue("random", false));
	private final BooleanValue value17 = addValue(new BooleanValue("small-caps", false));
	private final BooleanValue value18 = addValue(new BooleanValue("suffix", false));
	private final BooleanValue value19 = addValue(new BooleanValue("random 2", false));
	private final BooleanValue value20 = addValue(new BooleanValue("small-caps 2", true));

	public BetterChat() {
		super("Better Chat", 0, Category.MISC);
	}
}
