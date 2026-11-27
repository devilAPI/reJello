package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.mixin.MinecraftAccessor;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.Minecraft;

/** Removes the right-click cooldown. */
public class FastPlace extends Module {
	private final NumberValue speed = addValue(new NumberValue("Speed", 0, 0, 6, 1));

	public FastPlace() {
		super("FastPlace", 0, Category.WORLD);
	}

	@Override
	public void onUpdate() {
		if (!isToggled()) {
			return;
		}
		((MinecraftAccessor) Minecraft.getInstance()).jello$setRightClickDelay((int) speed.getValue());
	}

	@Override
	public void onDisable() {
		((MinecraftAccessor) Minecraft.getInstance()).jello$setRightClickDelay(4);
	}
}
