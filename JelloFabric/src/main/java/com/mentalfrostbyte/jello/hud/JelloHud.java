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

	public JelloHud() {
		EventManager.register(this);
	}

	private int miniMapOffset() {
		return Jello.isEnabled("MiniMap") ? 75 + 8 : 0;
	}

	@EventTarget
	public void onKey(EventKeyPressed e) {
		int y = miniMapOffset();
		InputConstants.Key key = e.getKey();
		var o = mc.options;
		if (matches(o.keyUp, key)) {
			w.addCircle(85 / 2f, 284 / 2f + y, 26, 5, o.keyUp, false);
		}
		if (matches(o.keyLeft, key)) {
			a.addCircle(34 / 2f, 334 / 2f + y, 26, 5, o.keyLeft, false);
		}
		if (matches(o.keyDown, key)) {
			s.addCircle(85 / 2f, 334 / 2f + y, 26, 5, o.keyDown, false);
		}
		if (matches(o.keyRight, key)) {
			d.addCircle(136 / 2f, 334 / 2f + y, 26, 5, o.keyRight, false);
		}
		if (matches(o.keyAttack, key)) {
			l.addCircle(47 / 2f, 386 / 2f + y, 35, 5, o.keyAttack, true);
		}
		if (matches(o.keyUse, key)) {
			r.addCircle(124 / 2f, 386 / 2f + y, 35, 5, o.keyUse, true);
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
		if (tab) {
			Render2D.image(g, "tabguishadow", 0.5f, 40.5f, 84, 86);
		}
		int yOff = 0;
		if (Jello.isEnabled("MiniMap")) {
			Render2D.image(g, "minimap", 0.5f, 251 / 2f, 84, 84);
			miniMap.render(g, 5, 130, 75);
			yOff += 75 + 8;
		}
		if (Jello.isEnabled("KeyStrokes")) {
			renderKeyStrokes(g, yOff);
			yOff += 100;
		}
		if (tab) {
			renderTabGui(g);
		}
	}

	private void renderKeyStrokes(GuiGraphicsExtractor g, int yOff) {
		Render2D.image(g, "keystrokes", 0.5f - 1, 251 / 2f - 1 + yOff, 86, 86);
		int kx = 5, ky = 130;
		key(g, w, kx + 25.5f, ky + yOff, kx + 49.5f, ky + 24 + yOff);
		key(g, a, kx, ky + 25.5f + yOff, kx + 24, ky + 49.5f + yOff);
		key(g, s, kx + 25.5f, ky + 25.5f + yOff, kx + 49.5f, ky + 49.5f + yOff);
		key(g, d, kx + 51, ky + 25.5f + yOff, kx + 75, ky + 49.5f + yOff);
		key(g, l, kx, ky + 51 + yOff, kx + 37, ky + 75 + yOff);
		key(g, r, kx + 38.5f, ky + 51 + yOff, kx + 75, ky + 75 + yOff);
	}

	/** The 1.8 code used a stencil of the key rectangle; a scissor does the same for these axis-aligned keys. */
	private void key(GuiGraphicsExtractor g, CircleManager circles, float x0, float y0, float x1, float y1) {
		Render2D.scissor(g, x0, y0, x1, y1);
		circles.drawCircles(g, 0);
		g.disableScissor();
	}

	private void renderTabGui(GuiGraphicsExtractor g) {
		int top = smoothColor(tCur, tLast), bottom = smoothColor(bCur, bLast);
		Render2D.gradient(g, 5, 45, 80, 122, top, bottom);
		TabGUI tabgui = Jello.tabgui;
		double fps = Anim.fpsFactor(5);
		if (tabgui.showModules) {
			Category category = tabgui.cats.get(tabgui.currentCategory);
			List<Module> modules = Jello.getModulesInCategory(category);
			int size = modules.size();
			float smoothTrans = Anim.smooth(category.selectedTrans, category.lastSelectedTrans);
			if (size != 0) {
				Render2D.gradient(g, 85, 45, 170, 45 + 15 * size + 2.5f, top, bottom);
				Render2D.imageUV(g, "tabguiselector", 85, 45 + smoothTrans, 85, 17.5f, 0, 0, 75, 17.5f, 75, 17.5f, -1);
			}
			int y = 0;
			for (Module m : modules) {
				if (y == 0) {
					Render2D.imageUV(g, "tabguishadow2", 80.5f, 40.5f, 94, 20, 0, 0, 94, 20, 94, 86, -1);
				} else if (y == size - 1) {
					Render2D.imageUV(g, "tabguishadow2", 80.5f, 40.5f + 15 * y + 5, 94, 20, 0, 64.5f, 94, 20, 94, 86, -1);
				} else {
					Render2D.imageUV(g, "tabguishadow2", 80.5f, 40.5f + 15 * y + 5, 94, 15, 0, 30, 94, 15, 94, 86, -1);
				}
				(m.isToggled() ? Fonts.jelloFontBoldSmall : Fonts.jelloFontMarker).drawString(g, m.getName(), 85 + 5.5f, 45 + 15 * y + 5, -1);
				y++;
			}
		}
		if (!Float.isFinite(tabgui.seenTrans)) {
			tabgui.seenTrans = 0;
		}
		for (int i = 0; i < 2; i++) {
			tabgui.seenTrans += (float) (((tabgui.currentCategory * 15) - tabgui.seenTrans) / fps - 0.001);
		}
		Render2D.image(g, "tabguiselector", 5, 45 + tabgui.seenTrans, 75, 17);
		int x = 0;
		for (Category c : tabgui.cats) {
			boolean selected = c == tabgui.cats.get(tabgui.currentCategory);
			if (!Float.isFinite(c.seenTrans)) {
				c.seenTrans = 0;
			}
			for (int i = 0; i < 2; i++) {
				c.seenTrans += (float) ((((selected ? 7 : 0) - c.seenTrans) / fps) + 0.001);
			}
			Render2D.image(g, c.texture, 5 + c.seenTrans, 45 + x * 15, 75, 17);
			x++;
		}
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
			Fonts.jelloFont.drawString(g, m.getDisplayName(), xStart, yStart + 7.5f, 0xffffffff);
			yStart += 7.5f + 5.25f;
		}
		g.pose().popMatrix();
	}

	/** Called on world join; the original reset its intro transition here. */
	public void onWorldLoad() {
		sampleTimer.reset();
	}
}
