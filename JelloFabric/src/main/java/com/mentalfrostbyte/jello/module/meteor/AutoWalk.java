package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.client.KeyMapping;

/** Meteor Client auto-walk: holds a movement key. */
public class AutoWalk extends Module {
	private final ModeValue direction = addValue(new ModeValue("Direction", "Forward", "Forward", "Backward", "Left", "Right"));

	public AutoWalk() {
		super("Auto Walk", 0, Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		releaseAll();
		switch (direction.getMode()) {
			case "Backward" -> mc.options.keyDown.setDown(true);
			case "Left" -> mc.options.keyLeft.setDown(true);
			case "Right" -> mc.options.keyRight.setDown(true);
			default -> mc.options.keyUp.setDown(true);
		}
	}

	@Override
	public void onDisable() {
		releaseAll();
	}

	private void releaseAll() {
		mc.options.keyUp.setDown(false);
		mc.options.keyDown.setDown(false);
		mc.options.keyLeft.setDown(false);
		mc.options.keyRight.setDown(false);
	}
}
