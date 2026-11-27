package com.mentalfrostbyte.jello.hud;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventKeyPressed;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.event.events.EventTick;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.ColorSampler;
import com.mentalfrostbyte.jello.render.Fonts;
import com.mentalfrostbyte.jello.render.RegionBlur;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.util.Anim;
import com.mentalfrostbyte.jello.util.ColorUtil;
import com.mentalfrostbyte.jello.util.TimerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** The in-game Jello overlay (GuiIngame hook in 1.8). */
public class JelloHud {
	private static final float KEY_X = 90;
	private static final float KEY_Y = 162;
	private final Minecraft mc = Minecraft.getInstance();
	private final TimerUtil sampleTimer = new TimerUtil();
	/** 0/1 TabGUI top-left/right, 2/3 TabGUI bottom-left/right, 4/5 notification top/bottom. */
	private final ColorSampler sampler = new ColorSampler(6);
	private final int[] tCur = new int[3], tLast = new int[3], bCur = new int[3], bLast = new int[3];
	private final int[] nCur = new int[3], nLast = new int[3], nbCur = new int[3], nbLast = new int[3];
	public final CircleManager w = new CircleManager(), a = new CircleManager(), s = new CircleManager(), d = new CircleManager();
	public final CircleManager l = new CircleManager(), r = new CircleManager();
	public final Compass compass = new Compass();
	public final MiniMapRenderer miniMap = new MiniMapRenderer();
	private final RegionBlur tabBlur = new RegionBlur("tabblur");
	private final RegionBlur listBlur = new RegionBlur("listblur");

	public JelloHud() {
		EventManager.register(this);
	}

	@EventTarget
	public void onKey(EventKeyPressed e) {
		InputConstants.Key key = e.getKey();
		var o = mc.options;
		if (matches(o.keyUp, key)) {
			w.addCircle(KEY_X + 37.5f, KEY_Y + 12, 26, 5, o.keyUp, false);
		}
		if (matches(o.keyLeft, key)) {
			a.addCircle(KEY_X + 12, KEY_Y + 37.5f, 26, 5, o.keyLeft, false);
		}
		if (matches(o.keyDown, key)) {
			s.addCircle(KEY_X + 37.5f, KEY_Y + 37.5f, 26, 5, o.keyDown, false);
		}
		if (matches(o.keyRight, key)) {
			d.addCircle(KEY_X + 63, KEY_Y + 37.5f, 26, 5, o.keyRight, false);
		}
		if (matches(o.keyAttack, key)) {
			l.addCircle(KEY_X + 18.5f, KEY_Y + 63, 35, 5, o.keyAttack, true);
		}
		if (matches(o.keyUse, key)) {
			r.addCircle(KEY_X + 56.5f, KEY_Y + 63, 35, 5, o.keyUse, true);
		}
	}

	private static boolean matches(KeyMapping mapping, InputConstants.Key key) {
		return KeyMappingHelper.getBoundKeyOf(mapping).equals(key);
	}

	@EventTarget
	public void onTick(EventTick e) {
		System.arraycopy(tCur, 0, tLast, 0, 3);
		System.arraycopy(bCur, 0, bLast, 0, 3);
		System.arraycopy(nCur, 0, nLast, 0, 3);
		System.arraycopy(nbCur, 0, nbLast, 0, 3);
		Notification n = NotificationManager.current();
		if (n != null) {
			n.onTick();
		}
		for (CircleManager c : new CircleManager[] {w, a, s, d, l, r}) {
			c.runCircles();
		}
		if (Jello.tabgui.showModules) {
			Category category = Jello.tabgui.cats.get(Jello.tabgui.currentCategory);
			category.lastSelectedTrans = category.selectedTrans;
			category.selectedTrans += ((category.selectedIndex * 15 - category.selectedTrans) / 2.5f) + 0.01f;
		}
		approach(tCur, ColorUtil.blend(sampler.get(0), sampler.get(1)));
		approach(bCur, ColorUtil.blend(sampler.get(2), sampler.get(3)));
		approach(nCur, sampler.get(4));
		approach(nbCur, sampler.get(5));
	}

	private static void approach(int[] channels, int argb) {
		int[] target = {(argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF};
		for (int i = 0; i < 3; i++) {
			channels[i] = Math.max(0, Math.min(255, (int) (channels[i] + (target[i] - channels[i]) / 5 + 0.1)));
		}
	}

	private static int smoothColor(int[] cur, int[] last) {
		return Render2D.color((int) Anim.smooth(cur[0], last[0]), (int) Anim.smooth(cur[1], last[1]), (int) Anim.smooth(cur[2], last[2]), 255);
	}

	public int notificationTop() {
		return smoothColor(nCur, nLast);
	}

	public int notificationBottom() {
		return smoothColor(nbCur, nbLast);
	}

	/** Fabric HudElement entry point. */
	public void render(GuiGraphicsExtractor g, DeltaTracker delta) {
		if (mc.player == null) {
			return;
		}
		if (sampleTimer.hasTimeElapsed(50, true)) {
			sampler.sample(0, 10, 45);
			sampler.sample(1, 70, 45);
			sampler.sample(2, 10, 45 + 77);
			sampler.sample(3, 70, 45 + 77);
			sampler.sample(4, g.guiWidth() - 10, 5);
			sampler.sample(5, g.guiWidth() - 10, 38);
		}
		EventManager.call(new EventRender2D(g, delta.getGameTimeDeltaPartialTick(false)));
		renderHud(g);
		if (Jello.isEnabled("ActiveMods")) {
			renderArraylist(g);
		}
		Notification n = NotificationManager.current();
		if (n != null) {
			n.render(g);
		}
		if (Jello.isEnabled("Compass")) {
			compass.draw(g);
		}
		// Closing animation of the ClickGUI is drawn over the HUD once the screen is gone.
		Jello.jgui.renderOutro(g);
	}

	private void renderHud(GuiGraphicsExtractor g) {
		Render2D.image(g, "jello", -0.5f, -0.5f, 86, 49);
		boolean tab = Jello.isEnabled("TabGUI");
		if (Jello.isEnabled("MiniMap")) {
			// Sigma layout: minimap directly under the category list, keys beside it.
			Render2D.image(g, "minimap", 0.5f, 160.5f, 84, 84);
			miniMap.render(g, 5, 165, 75);
		}
		if (Jello.isEnabled("KeyStrokes")) {
			renderKeyStrokes(g);
		}
		if (tab) {
			renderTabGui(g);
		}
	}

	private void renderKeyStrokes(GuiGraphicsExtractor g) {
		float kx = KEY_X, ky = KEY_Y;
		glass(g, kx - 4, ky - 4, 84, 84, 0.9f);
		keyBox(g, kx + 25.5f, ky + 0.5f, kx + 49.5f, ky + 24.5f, "W");
		keyBox(g, kx + 0.5f, ky + 25.5f, kx + 24.5f, ky + 49.5f, "A");
		keyBox(g, kx + 25.5f, ky + 25.5f, kx + 49.5f, ky + 49.5f, "S");
		keyBox(g, kx + 51f, ky + 25.5f, kx + 75f, ky + 49.5f, "D");
		keyBox(g, kx + 0.5f, ky + 51f, kx + 37f, ky + 75f, "L");
		keyBox(g, kx + 38.5f, ky + 51f, kx + 75f, ky + 75f, "R");
		key(g, w, kx + 25.5f, ky + 0.5f, kx + 49.5f, ky + 24.5f);
		key(g, a, kx + 0.5f, ky + 25.5f, kx + 24.5f, ky + 49.5f);
		key(g, s, kx + 25.5f, ky + 25.5f, kx + 49.5f, ky + 49.5f);
		key(g, d, kx + 51f, ky + 25.5f, kx + 75f, ky + 49.5f);
		key(g, l, kx + 0.5f, ky + 51f, kx + 37f, ky + 75f);
		key(g, r, kx + 38.5f, ky + 51f, kx + 75f, ky + 75f);
		for (String[] label : new String[][] {{"W", String.valueOf(kx + 37.5f), String.valueOf(ky + 8.5f)},
			{"A", String.valueOf(kx + 12f), String.valueOf(ky + 33.5f)},
			{"S", String.valueOf(kx + 37.5f), String.valueOf(ky + 33.5f)},
			{"D", String.valueOf(kx + 63f), String.valueOf(ky + 33.5f)},
			{"L", String.valueOf(kx + 18.5f), String.valueOf(ky + 59f)},
			{"R", String.valueOf(kx + 56.5f), String.valueOf(ky + 59f)}}) {
			Fonts.jelloFontMedium12.drawCenteredShadow(g, label[0], Float.parseFloat(label[1]), Float.parseFloat(label[2]), 0xFFFFFFFF);
		}
	}

	private void keyBox(GuiGraphicsExtractor g, float x0, float y0, float x1, float y1, String label) {
		Render2D.roundedRectBorder(g, x0, y0, x1 - x0, y1 - y0, 3,
			alpha(0x33FFFFFF, 0.9f), alpha(0x18191F, 0.55f));
	}

	/** The 1.8 code used a stencil of the key rectangle; a scissor does the same for these axis-aligned keys. */
	private void key(GuiGraphicsExtractor g, CircleManager circles, float x0, float y0, float x1, float y1) {
		Render2D.scissor(g, x0, y0, x1, y1);
		circles.drawCircles(g, 0);
		g.disableScissor();
	}

	private void renderTabGui(GuiGraphicsExtractor g) {
		TabGUI tabgui = Jello.tabgui;
		double fps = Anim.fpsFactor(5);
		float panelH = tabgui.cats.size() * 15 + 10;
		tabBlur.sample(5, 45, 80, panelH);
		Render2D.image(g, tabBlur.id(), 5, 45, 80, panelH, -1);
		glass(g, 5, 45, 80, panelH, 1f);

		if (!Float.isFinite(tabgui.seenTrans)) {
			tabgui.seenTrans = 0;
		}
		for (int i = 0; i < 2; i++) {
			tabgui.seenTrans += (float) (((tabgui.currentCategory * 15) - tabgui.seenTrans) / fps - 0.001);
		}
		Render2D.roundedRect(g, 8, 50 + tabgui.seenTrans, 74, 14, 4, alpha(0xFF2F8FF7, 0.85f));

		int x = 0;
		for (Category c : tabgui.cats) {
			boolean selected = x == tabgui.currentCategory;
			Fonts.jelloFontRegular.drawStringShadow(g, c.name, 12, 52.5f + x * 15,
				selected ? 0xFFFFFFFF : 0xFFB9BFCE);
			x++;
		}

		if (tabgui.showModules) {
			Category category = tabgui.cats.get(tabgui.currentCategory);
			List<Module> modules = Jello.getModulesInCategory(category);
			int size = modules.size();
			int visible = Math.min(8, size);
			int start = size <= visible ? 0 : Math.max(0, Math.min(category.selectedIndex - visible / 2, size - visible));
			if (visible > 0) {
				float listH = visible * 15 + 10;
				listBlur.sample(88, 45, 92, listH);
				Render2D.image(g, listBlur.id(), 88, 45, 92, listH, -1);
				glass(g, 88, 45, 92, listH, 1f);
				float smooth = Anim.smooth(category.selectedTrans, category.lastSelectedTrans) - start * 15f;
				Render2D.roundedRect(g, 91, 50 + smooth, 86, 14, 4, alpha(0xFF2F8FF7, 0.22f));
				Render2D.roundedRect(g, 91, 50 + smooth, 2, 14, 1, alpha(0xFF2F8FF7, 0.95f));
				for (int row = 0; row < visible; row++) {
					Module m = modules.get(start + row);
					Fonts.jelloFontRegular.drawStringShadow(g, m.getName(), 97, 52.5f + row * 15,
						m.isToggled() ? 0xFFFFFFFF : 0xFFC3C8D4);
				}
			}
		}
	}

	private static void glass(GuiGraphicsExtractor g, float x, float y, float w, float h, float alpha) {
		Render2D.roundedRect(g, x - 3, y - 3, w + 6, h + 6, 9, alpha(0x000000, 0.35f * alpha));
		Render2D.roundedRectBorder(g, x, y, w, h, 7, alpha(0x33FFFFFF, alpha), alpha(0xA80F1016, alpha));
		Render2D.roundedRect(g, x + 1, y + 1, w - 2, h * 0.45f, 6, alpha(0x0DFFFFFF, alpha));
		Render2D.roundedRect(g, x + 10, y, w - 20, 1, 0, alpha(0xFF2F8FF7, 0.65f * alpha));
	}

	private static int alpha(int color, float a) {
		int ca = (int) (((color >>> 24) & 0xFF) * Math.max(0, Math.min(1, a)));
		return (ca << 24) | (color & 0x00FFFFFF);
	}

	private void renderArraylist(GuiGraphicsExtractor g) {
		List<Module> enabled = new ArrayList<>();
		for (Module m : Jello.getModules()) {
			if (m.isToggled()) {
				enabled.add(m);
			}
		}
		enabled.sort(Comparator.comparingDouble((Module m) -> Fonts.jelloFont.getStringWidth(m.getDisplayName())).reversed());
		Notification n = NotificationManager.current();
		float shift = n != null && !n.isDone() ? n.getProgress() : 0;
		g.pose().pushMatrix();
		g.pose().translate(0, shift);
		float yStart = 1;
		for (Module m : enabled) {
			float width = Fonts.jelloFont.getStringWidth(m.getDisplayName());
			float xStart = g.guiWidth() - width - 5;
			Render2D.image(g, "arraylistshadow", xStart - 11, yStart + 2 - 2.5f - 4.5f - 6 - 1, width + 30, 38.5f, Render2D.white(0.7f));
			yStart += 7.5f + 5.25f;
		}
		yStart = 1;
		for (Module m : enabled) {
			float xStart = g.guiWidth() - Fonts.jelloFont.getStringWidth(m.getDisplayName()) - 5;
			Fonts.jelloFontRegular.drawStringShadow(g, m.getDisplayName(), xStart, yStart + 7.5f, 0xffffffff);
			yStart += 7.5f + 5.25f;
		}
		g.pose().popMatrix();
	}

	/** Called on world join; the original reset its intro transition here. */
	public void onWorldLoad() {
		sampleTimer.reset();
	}
}
