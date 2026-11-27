package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Fonts;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Meteor Client nametags: projected player names and health. */
public class Nametags extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 64, 8, 256, 8));
	private final NumberValue scale = addValue(new NumberValue("Scale", 1.0, 0.5, 2.0, 0.1));

	public Nametags() {
		super("Nametags", 0, Category.RENDER);
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		for (Player player : mc.level.players()) {
			if (player == mc.player || player.isInvisible()) {
				continue;
			}
			double distance = mc.player.distanceTo(player);
			if (distance > range.getValue()) {
				continue;
			}
			Vec3 head = mc.gameRenderer.projectPointToScreen(player.position().add(0, player.getBbHeight() + 0.4, 0));
			if (head.z < -1 || head.z > 1) {
				continue;
			}
			float x = (float) ((head.x * 0.5 + 0.5) * event.graphics.guiWidth());
			float y = (float) ((-head.y * 0.5 + 0.5) * event.graphics.guiHeight());
			String text = player.getName().getString() + " " + Math.round(player.getHealth());
			Fonts.jelloFontRegular.drawCenteredShadow(event.graphics, text, x, y, 0xFFFFFFFF);
			float health = Math.max(0, player.getHealth() / player.getMaxHealth());
			com.mentalfrostbyte.jello.render.Render2D.rect(event.graphics, x - 20, y + 10, x + 20, y + 12, 0x88000000);
			com.mentalfrostbyte.jello.render.Render2D.rect(event.graphics, x - 20, y + 10, x - 20 + 40 * health, y + 12,
				com.mentalfrostbyte.jello.render.Render2D.color(1f, 0.2f, 0.2f, 0.9f));
		}
	}
}
