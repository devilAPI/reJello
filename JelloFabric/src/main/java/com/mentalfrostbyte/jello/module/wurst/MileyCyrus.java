package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.KeyMapping;

/** Wurst MileyCyrus: rapidly toggles the sneak key. */
public class MileyCyrus extends Module {
	private final NumberValue twerkSpeed = addValue(new NumberValue("Twerk Speed", 5, 1, 10, 1));

	private int timer;

	public MileyCyrus() {
		super("Miley Cyrus", 0, Category.MISC);
	}

	@Override
	public void onEnable() {
		timer = 0;
	}

	@Override
	public void onDisable() {
		mc.options.keyShift.setDown(false);
	}

	@Override
	public void onUpdate() {
		if (!isToggled()) {
			return;
		}
		timer++;
		if (timer < 10 - twerkSpeed.getValue()) {
			return;
		}
		KeyMapping sneakKey = mc.options.keyShift;
		sneakKey.setDown(!sneakKey.isDown());
		timer = -1;
	}
}
