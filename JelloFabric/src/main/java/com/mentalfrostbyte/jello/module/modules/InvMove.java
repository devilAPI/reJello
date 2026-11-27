package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

/** Lets movement keys work while a container or the ClickGUI is open. */
public class InvMove extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Vanilla", "Vanilla", "NoPacket"));
	public static boolean en;

	public InvMove() {
		super("InvMove", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		en = true;
	}

	@Override
	public void onDisable() {
		en = false;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		if (!(mc.gui.screen() instanceof AbstractContainerScreen<?>) && !(mc.gui.screen() instanceof com.mentalfrostbyte.jello.gui.JelloGui)) {
			return;
		}
		KeyMapping[] keys = {mc.options.keyUp, mc.options.keyDown, mc.options.keyLeft, mc.options.keyRight,
			mc.options.keyJump, mc.options.keySprint};
		for (KeyMapping mapping : keys) {
			InputConstants.Key key = KeyMappingHelper.getBoundKeyOf(mapping);
			mapping.setDown(InputConstants.isKeyDown(key.getValue()));
		}
	}
}
