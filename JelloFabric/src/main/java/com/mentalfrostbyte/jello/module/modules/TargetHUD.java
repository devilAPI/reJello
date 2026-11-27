package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Fonts;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.world.entity.LivingEntity;

/** Draws the KillAura target's name and health at the configured position. */
public class TargetHUD extends Module {
	private final NumberValue yAxis = addValue(new NumberValue("Y axis", 300, -500, 600, 1));
	private final NumberValue xAxis = addValue(new NumberValue("X axis", 500, -200, 1000, 1));

	public TargetHUD() {
		super("TargetHUD", 0, Category.RENDER);
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || KillAura.getTarget() == null) {
			return;
		}
		LivingEntity target = KillAura.getTarget();
		float x = (float) xAxis.getValue() / 2f;
		float y = (float) yAxis.getValue() / 2f;
		Render2D.rect(event.graphics, x - 60, y - 15, x + 60, y + 15, Render2D.color(0, 0, 0, 0.55f));
		Fonts.jelloFontRegular.drawStringShadow(event.graphics, target.getName().getString(), x - 55, y - 10, 0xFFFFFFFF);
		float health = Math.max(0, target.getHealth() / target.getMaxHealth());
		Render2D.rect(event.graphics, x - 55, y + 3, x + 55, y + 9, Render2D.color(0, 0, 0, 0.4f));
		Render2D.rect(event.graphics, x - 55, y + 3, x - 55 + 110 * health, y + 9, Render2D.color(1f, 0.2f, 0.2f, 0.9f));
	}
}
