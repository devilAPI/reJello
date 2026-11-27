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
import net.minecraft.world.phys.Vec3;

/** Simple 2D entity boxes, keyed by angle/distance instead of a full 3D projection. */
public class ESP extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	private final ModeValue mode = addValue(new ModeValue("Mode", "Fill", "Fill", "Box", "Outline", "2D", "CSGO"));
	private final ModeValue colorMode = addValue(new ModeValue("ColorMode", "Health", "Health", "Team", "Custom", "Rainbow", "SigmaTest"));
	private final BooleanValue armor = addValue(new BooleanValue("Armor", true));

	public ESP() {
		super("ESP", 0, Category.RENDER);
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameRenderer == null) {
			return;
		}
		float guiW = event.graphics.guiWidth();
		float guiH = event.graphics.guiHeight();
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof LivingEntity living) || entity == mc.player) {
				continue;
			}
			// Players and hostile mobs only: fish/sheep boxes made the overlay unreadable.
			if (!(living instanceof Player) && !(living instanceof net.minecraft.world.entity.monster.Monster)) {
				continue;
			}
			double distance = mc.player.distanceTo(entity);
			if (distance > 64) {
				continue;
			}
			Vec3 feet = mc.gameRenderer.projectPointToScreen(entity.position());
			Vec3 head = mc.gameRenderer.projectPointToScreen(entity.position().add(0, entity.getBbHeight(), 0));
			if (feet.z < -1 || feet.z > 1 || head.z < -1 || head.z > 1) {
				continue;
			}
			float x = (float) ((feet.x * 0.5 + 0.5) * guiW);
			float headY = (float) ((-head.y * 0.5 + 0.5) * guiH);
			float feetY = (float) ((-feet.y * 0.5 + 0.5) * guiH);
			float height = Math.max(8, Math.abs(feetY - headY));
			float width = height * 0.55f;
			float y = (headY + feetY) / 2f;
			int color = living instanceof Player ? 0xFFFF5555 : 0xFF55FF55;
			float x0 = x - width / 2, x1 = x + width / 2;
			float y0 = y - height / 2, y1 = y + height / 2;
			if (mode.is("Fill") || mode.is("2D")) {
				Render2D.rect(event.graphics, x0, y0, x1, y1, (color & 0x00FFFFFF) | 0x33000000);
			}
			if (mode.is("Box") || mode.is("Outline") || mode.is("2D")) {
				Render2D.rect(event.graphics, x0, y0, x1, y0 + 1.5f, color);
				Render2D.rect(event.graphics, x0, y1 - 1.5f, x1, y1, color);
				Render2D.rect(event.graphics, x0, y0, x0 + 1.5f, y1, color);
				Render2D.rect(event.graphics, x1 - 1.5f, y0, x1, y1, color);
			}
			if (mode.is("CSGO")) {
				float corner = Math.min(7, width / 3);
				Render2D.rect(event.graphics, x0, y0, x0 + corner, y0 + 1.5f, color);
				Render2D.rect(event.graphics, x0, y0, x0 + 1.5f, y0 + corner, color);
				Render2D.rect(event.graphics, x1 - corner, y0, x1, y0 + 1.5f, color);
				Render2D.rect(event.graphics, x1 - 1.5f, y0, x1, y0 + corner, color);
				Render2D.rect(event.graphics, x0, y1 - 1.5f, x0 + corner, y1, color);
				Render2D.rect(event.graphics, x0, y1 - corner, x0 + 1.5f, y1, color);
				Render2D.rect(event.graphics, x1 - corner, y1 - 1.5f, x1, y1, color);
				Render2D.rect(event.graphics, x1 - 1.5f, y1 - corner, x1, y1, color);
			}
			if (armor.getValue() && living instanceof Player) {
				float armorY = y + height / 2 + 3;
				Render2D.rect(event.graphics, x - width / 2, armorY, x + width / 2, armorY + 1.5f, 0x66000000);
			}
		}
	}
}
