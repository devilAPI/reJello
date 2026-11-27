package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Book Bot). Behaviour port pending. */
public class BookBot extends Module {
	private final ModeValue value1 = addValue(new ModeValue("mode", "Random", "Random", "C", "T", "B"));
	private final ModeValue value2 = addValue(new ModeValue("random-type", "Utf8", "Utf8", "A", "U", "P"));
	private final NumberValue value3 = addValue(new NumberValue("pages", 50, 1, 100, 1));
	private final NumberValue value4 = addValue(new NumberValue("characters", 128, 1, 1024, 1));
	private final NumberValue value5 = addValue(new NumberValue("delay", 20, 1, 200, 1));
	private final BooleanValue value6 = addValue(new BooleanValue("sign", true));
	private final BooleanValue value7 = addValue(new BooleanValue("append-count", true));
	private final BooleanValue value8 = addValue(new BooleanValue("word-wrap", true));

	public BookBot() {
		super("Book Bot", 0, Category.MISC);
	}
}
