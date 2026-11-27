package com.mentalfrostbyte.jello.hud;

import com.mentalfrostbyte.jello.render.Fonts;
import com.mentalfrostbyte.jello.render.Render2D;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Horizontal compass tape at the top centre of the screen. */
public class Compass {
	private record Degree(String text, int type) {
	}

	private final List<Degree> degrees = new ArrayList<>();

	public Compass() {
		String[][] marks = {{"N", "1"}, {"195", "2"}, {"210", "2"}, {"NE", "3"}, {"240", "2"}, {"255", "2"}, {"E", "1"}, {"285", "2"},
			{"300", "2"}, {"SE", "3"}, {"330", "2"}, {"345", "2"}, {"S", "1"}, {"15", "2"}, {"30", "2"}, {"SW", "3"}, {"60", "2"},
			{"75", "2"}, {"W", "1"}, {"105", "2"}, {"120", "2"}, {"NW", "3"}, {"150", "2"}, {"165", "2"}};
		for (String[] m : marks) {
			degrees.add(new Degree(m[0], Integer.parseInt(m[1])));
		}
	}

	public void draw(GuiGraphicsExtractor g) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			return;
		}
		int width = g.guiWidth();
		Render2D.image(g, "shadow", width / 2f - 325.5f / 2, -8, 325.5f, 76.5f);
		float center = width / 2f;
		float yaw = mc.player.getYRot(mc.getDeltaTracker().getGameTimeDeltaPartialTick(false));
		float rewind = (yaw % 360) * 2 + 360 * 3;
		int count = 0;
		// Three passes so the tape wraps around seamlessly, exactly like the original.
		for (int pass = 0; pass < 3; pass++) {
			for (Degree d : degrees) {
				float location = center + count * 30 - rewind;
				float complete = switch (d.type) {
					case 1 -> location - Fonts.jelloFontBig.getStringWidth(d.text) / 2;
					case 2 -> location - Fonts.jelloFontMarker.getStringWidth(d.text) / 2;
					default -> location - Fonts.jelloFontMedium.getStringWidth(d.text) / 2;
				};
				int opacity = opacity(width, complete);
				if ((opacity >>> 24) != 0) {
					switch (d.type) {
						case 1 -> Fonts.jelloFontBig.drawStringShadow(g, d.text, complete, 25, opacity);
						case 2 -> {
							Render2D.rect(g, location - 0.5f, 29, location + 0.5f, 34, opacity);
							Fonts.jelloFontMarker.drawStringShadow(g, d.text, complete, 33.5f + 4, opacity);
						}
						default -> Fonts.jelloFontMedium.drawStringShadow(g, d.text, complete,
							25 + Fonts.jelloFontBig.getHeight() / 2 - Fonts.jelloFontMedium.getHeight() / 2, opacity);
					}
				}
				count++;
			}
		}
	}

	private static int opacity(int width, float offset) {
		float offs = 255 - Math.abs(width / 2f - offset) * 1.8f;
		int a = (int) Math.min(Math.max(0, offs), 255);
		return (a << 24) | 0xFFFFFF;
	}
}
