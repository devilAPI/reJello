package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Simple 2D entity boxes, keyed by angle/distance instead of a full 3D projection. */
public class ESP extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Fill", "Fill", "Box", "Outline", "2D", "CSGO"));
	private final ModeValue colorMode = addValue(new ModeValue("ColorMode", "Health", "Health", "Team", "Custom", "Rainbow", "SigmaTest"));
	private final BooleanValue armor = addValue(new BooleanValue("Armor", true));

	public ESP() {
		super("ESP", 0, Category.RENDER);
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		float centerX = event.graphics.guiWidth() / 2f;
		float centerY = event.graphics.guiHeight() / 2f;
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof LivingEntity living) || entity == mc.player) {
				continue;
			}
			double distance = mc.player.distanceTo(entity);
			if (distance > 48) {
				continue;
			}
			double dx = entity.getX() - mc.player.getX();
			double dz = entity.getZ() - mc.player.getZ();
			float angle = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
			float relative = angle - mc.player.getYRot();
			double radians = Math.toRadians(relative);
			float x = centerX + (float) Math.sin(radians) * (float) (220 / Math.max(1, distance / 8));
			float y = centerY - (float) Math.cos(radians) * (float) (140 / Math.max(1, distance / 8));
			float size = (float) Math.max(8, 60 / Math.max(1, distance / 8));
			int color = living instanceof Player ? 0xAAFF5555 : 0xAA55FF55;
			if (mode.is("Outline")) {
				Render2D.rect(event.graphics, x - size, y - size, x + size, y + size, 0x66000000);
			} else {
				Render2D.rect(event.graphics, x - size, y - size, x + size, y + size, (color & 0x00FFFFFF) | 0x33000000);
			}
		}
	}
}
