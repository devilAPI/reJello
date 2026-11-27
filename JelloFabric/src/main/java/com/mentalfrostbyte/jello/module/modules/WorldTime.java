package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.multiplayer.ClientLevel;

/** Sets the client world time. */
public class WorldTime extends Module {
	private final NumberValue time = addValue(new NumberValue("Time", 24000, 1, 24000, 1));

	public WorldTime() {
		super("WorldTime", 0, Category.RENDER);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || !(mc.level instanceof ClientLevel level)) {
			return;
		}
		level.getLevelData().setGameTime((long) time.getValue());
	}
}
