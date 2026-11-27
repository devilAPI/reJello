package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Notifier). Behaviour port pending. */
public class Notifier extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("totem-pops", true));
	private final BooleanValue value2 = addValue(new BooleanValue("distance-check", false));
	private final NumberValue value3 = addValue(new NumberValue("player-radius", 30, 1, 100, 1));
	private final BooleanValue value4 = addValue(new BooleanValue("ignore-own", false));
	private final BooleanValue value5 = addValue(new BooleanValue("ignore-friends", false));
	private final BooleanValue value6 = addValue(new BooleanValue("ignore-others", false));
	private final BooleanValue value7 = addValue(new BooleanValue("visual-range", false));
	private final ModeValue value8 = addValue(new ModeValue("event", "Both", "Both", "S", "D", "B"));
	private final BooleanValue value9 = addValue(new BooleanValue("ignore-friends 2", true));
	private final BooleanValue value10 = addValue(new BooleanValue("ignore-fake-players", true));
	private final BooleanValue value11 = addValue(new BooleanValue("sound", true));
	private final BooleanValue value12 = addValue(new BooleanValue("pearl", true));
	private final BooleanValue value13 = addValue(new BooleanValue("ignore-own 2", false));
	private final BooleanValue value14 = addValue(new BooleanValue("ignore-friends 3", false));
	private final ModeValue value15 = addValue(new ModeValue("player-joins-leaves", "None", "None", "N"));
	private final NumberValue value16 = addValue(new NumberValue("notification-delay", 0, 0, 1000, 1));
	private final BooleanValue value17 = addValue(new BooleanValue("simple-notifications", true));

	public Notifier() {
		super("Notifier", 0, Category.MISC);
	}
}
