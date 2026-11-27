package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Fonts;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Wurst HealthTags: adds a coloured health number to player nametags. */
public class HealthTags extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 64, 8, 256, 8));
	private final BooleanValue showNames = addValue(new BooleanValue("Show Names", true));

	public HealthTags() {
		super("Health Tags", 0, Category.RENDER);
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameRenderer == null) {
			return;
		}
		for (Player player : mc.level.players()) {
			if (player == mc.player || player.isInvisible()) {
				continue;
			}
			if (mc.player.distanceTo(player) > range.getValue()) {
				continue;
			}
			Vec3 head = mc.gameRenderer.projectPointToScreen(
				player.position().add(0, player.getBbHeight() + 0.6, 0));
			if (head.z < -1 || head.z > 1 || Double.isNaN(head.x) || Double.isNaN(head.y)) {
				continue;
			}
			float x = (float) ((head.x * 0.5 + 0.5) * event.graphics.guiWidth());
			float y = (float) ((-head.y * 0.5 + 0.5) * event.graphics.guiHeight());
			String text = Integer.toString((int) player.getHealth());
			if (showNames.getValue()) {
				text = player.getName().getString() + " " + text;
			}
			Fonts.jelloFontRegular.drawCenteredShadow(event.graphics, text, x, y, color(player.getHealth()));
		}
	}

	private static int color(float health) {
		if (health <= 5) {
			return 0xFFAA0000;
		}
		if (health <= 10) {
			return 0xFFFFAA00;
		}
		if (health <= 15) {
			return 0xFFFFFF55;
		}
		return 0xFF55FF55;
	}
}
