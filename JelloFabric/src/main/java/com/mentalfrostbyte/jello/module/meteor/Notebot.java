package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Meteor Client module (Notebot). Behaviour port pending. */
public class Notebot extends Module {
	private final NumberValue value1 = addValue(new NumberValue("tick-delay", 1, 1, 20, 1));
	private final NumberValue value2 = addValue(new NumberValue("concurrent-tune-blocks", 1, 1, 20, 1));
	private final ModeValue value3 = addValue(new ModeValue("mode", "ExactInstruments", "ExactInstruments"));
	private final ModeValue value4 = addValue(new ModeValue("instrument-detect-mode", "BlockState", "BlockState", "B", "B"));
	private final BooleanValue value5 = addValue(new BooleanValue("polyphonic", true));
	private final BooleanValue value6 = addValue(new BooleanValue("auto-rotate", true));
	private final BooleanValue value7 = addValue(new BooleanValue("auto-play", false));
	private final BooleanValue value8 = addValue(new BooleanValue("transpose-out-of-range", false));
	private final BooleanValue value9 = addValue(new BooleanValue("swing-arm", true));
	private final NumberValue value10 = addValue(new NumberValue("check-noteblocks-again-delay", 10, 1, 20, 1));
	private final BooleanValue value11 = addValue(new BooleanValue("render-text", true));
	private final BooleanValue value12 = addValue(new BooleanValue("render-boxes", true));
	private final ModeValue value13 = addValue(new ModeValue("shape-mode", "Both", "Both", "L", "S", "B"));
	private final NumberValue value14 = addValue(new NumberValue("note-text-scale", 1.5, 0.0, 100, 0.1));
	private final BooleanValue value15 = addValue(new BooleanValue("show-scanned-noteblocks", false));

	public Notebot() {
		super("Notebot", 0, Category.MISC);
	}
}
