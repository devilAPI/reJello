package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Swing/inventory animation settings. The 1.18 renderer wrappers are not ported. */
public class Animations extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Leaked", "Jello", "Exhibition", "Slide", "OldSigma", "Leaked", "Exhibition2", "Slide2", "Swang"));
	private final NumberValue speed = addValue(new NumberValue("AnimationSpeed", 1.0, 1.0, 6.0, 0.1));
	private final ModeValue inventory = addValue(new ModeValue("InventoryAnimation", "SlideUp", "Rotate", "SlideUp", "None"));

	public Animations() {
		super("Animations", 0, Category.RENDER);
	}
}
