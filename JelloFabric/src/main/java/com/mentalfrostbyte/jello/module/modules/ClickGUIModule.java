package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mojang.blaze3d.platform.InputConstants;

/** Opens the ClickGUI. The setting set matches Reborn's ClickGUI module. */
public class ClickGUIModule extends Module {
	private final ModeValue theme = addValue(new ModeValue("Theme", "Light", "Light", "Dark"));
	private final BooleanValue blur = addValue(new BooleanValue("Blur", false));
	private final BooleanValue particles = addValue(new BooleanValue("Particles", true));
	private final ModeValue textPos = addValue(new ModeValue("TextPos", "Center", "Center", "Right"));
	private final ModeValue style = addValue(new ModeValue("GuiModeThingIdk", "Jello", "Jello", "Sigma"));

	public ClickGUIModule() {
		super("ClickGUI", InputConstants.KEY_RSHIFT, Category.RENDER);
	}

	@Override
	public void onEnable() {
		if (mc.level != null && mc.gui.screen() == null) {
			mc.gui.setScreen(Jello.jgui);
			setToggled(false);
		}
	}
}
