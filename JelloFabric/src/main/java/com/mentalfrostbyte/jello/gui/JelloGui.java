package com.mentalfrostbyte.jello.gui;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Fonts;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.setting.Value;
import com.mentalfrostbyte.jello.util.Anim;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * The Jello ClickGUI. Port of {@code jelloclickgui.JelloGui}/{@code Panel}: five category panels, drag and
 * scroll, left click toggles, right click opens the settings popup with checkboxes and sliders.
 *
 * <p>The panels live on a fixed 640x210 virtual canvas that is scaled down to the current GUI size, so the
 * window size and GUI scale no longer change how big the menu looks. The popup and the closing animation are
 * kept inside that canvas as well.
 */
public class JelloGui extends Screen {
	private static final float PANEL_W = 100;
	private static final float HEADER_H = 25;
	private static final float ROW_H = 15;
	private static final float PANEL_H = 100;
	private static final float LIST_H = 4 * 15;
	private static final float BORDER = 9.5f;
	private static final float VIRTUAL_W = 480;
	private static final float VIRTUAL_H = 240;
	private static final float PANEL_GAP = 19;

	private final Minecraft mc = Minecraft.getInstance();
	public final List<Panel> panels = new ArrayList<>();
	public int mouseX;
	public int mouseY;
	public Module selectedModule;
	public Panel selectedCategory;
	public NumberValue selectedValue;
	public boolean exit;
	public float percent = 1;
	public float lastPercent = 1;
	public float percent2 = 0.98f;
	public float lastPercent2 = 0.98f;
	public float outro = 1;
	public float lastOutro = 1;
	public float settingsTrans;
	public float lastSettingsTrans;
	public int settingsPage;
	public float scrollDelta;
	/** True while the close animation runs after the screen has been dismissed. */
	private boolean closing;
	/** True once the screen has actually been shown; prevents the close animation from running at startup. */
	private boolean wasOpen;
	private final List<Particle> particles = new ArrayList<>();
	private final Random random = new Random();

	public JelloGui() {
		super(Component.literal("Jello"));
		for (int i = 0; i < 42; i++) {
			particles.add(new Particle(random.nextFloat() * VIRTUAL_W, random.nextFloat() * VIRTUAL_H,
				(random.nextFloat() - 0.5f) * 0.35f, (random.nextFloat() - 0.5f) * 0.35f, 0.6f + random.nextFloat() * 1.4f));
		}
		for (Category category : Category.values()) {
			panels.add(new Panel(0, 10, category));
		}
		layoutPanels();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean isInGameUi() {
		return minecraft != null && minecraft.level != null;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		if (minecraft == null || minecraft.level == null) {
			super.extractBackground(g, mouseX, mouseY, partialTick);
			return;
		}
		if (blurEnabled()) {
			try {
				g.blurBeforeThisStratum();
			} catch (IllegalStateException ignored) {
				// One blur per frame; the vanilla menu background may already have queued it.
			}
		}
		g.fillGradient(0, 0, width, height, Render2D.color(0.04f, 0.07f, 0.12f, 0.38f), Render2D.color(0.01f, 0.01f, 0.03f, 0.58f));
		Render2D.circle(g, width * 0.18f, height * 0.10f, Math.max(width, height) * 0.55f,
			Render2D.color(0.20f, 0.48f, 0.95f, 0.05f));
		extractMenuBackground(g);
	}

	@Override
	protected void init() {
		super.init();
		percent = 1.23f;
		lastPercent = 1.23f;
		percent2 = 0.98f;
		lastPercent2 = 0.98f;
		outro = 1;
		lastOutro = 1;
		settingsTrans = 0;
		lastSettingsTrans = 0;
		settingsPage = 0;
		selectedModule = null;
		selectedCategory = null;
		selectedValue = null;
		exit = false;
		closing = false;
		wasOpen = true;
		for (Panel panel : panels) {
			panel.dragging = false;
			panel.scroll = 0;
			panel.lastScroll = 0;
			panel.scrollFade = 0;
		}
	}

	/** Called from the client tick: runs the intro/outro and per-panel hover animations. */
	public void onTick() {
		lastOutro = outro;
		lastPercent = percent;
		lastPercent2 = percent2;
		lastSettingsTrans = settingsTrans;
		boolean open = mc != null && mc.gui.screen() == this;
		if (open) {
			wasOpen = true;
			outro = 1;
			lastOutro = 1;
			closing = false;
		} else if (wasOpen) {
			wasOpen = false;
			closing = true;
		}
		if (closing) {
			outro += 0.1f + (1.7f - outro) / 3f - 0.001f;
			if (outro >= 1.7f) {
				outro = 1.7f;
				closing = false;
			}
			return;
		}
		if (!open) {
			return;
		}
		if (percent > 0.98f) {
			percent += (0.98f - percent) / 1.45f - 0.001f;
		} else if (percent2 < 1) {
			percent2 += (1 - percent2) / 2.8f + 0.002f;
		}
		if (selectedModule != null && selectedCategory != null && !exit) {
			settingsTrans += (1 - settingsTrans) / 1.8f + 0.01f;
		}
		if (exit && settingsTrans > 0) {
			settingsTrans += (0 - settingsTrans) / 1.8f - 0.01f;
		}
		if (settingsTrans <= 0.001f && exit) {
			exit = false;
			selectedModule = null;
			selectedCategory = null;
			selectedValue = null;
		}
		settingsTrans = Math.max(0, Math.min(1, settingsTrans));
		for (Particle particle : particles) {
			particle.x += particle.vx;
			particle.y += particle.vy;
			if (particle.x < 0) particle.x += VIRTUAL_W;
			if (particle.x > VIRTUAL_W) particle.x -= VIRTUAL_W;
			if (particle.y < 0) particle.y += VIRTUAL_H;
			if (particle.y > VIRTUAL_H) particle.y -= VIRTUAL_H;
		}
		for (Panel panel : panels) {
			panel.onTick();
		}
	}

	/** Theme/text/particle settings come from the Reborn ClickGUI module. */
	private static boolean isDark() {
		Module module = Jello.getModule("ClickGUI");
		Value value = module == null ? null : module.getValue("Theme");
		return value instanceof ModeValue mode && mode.is("Dark");
	}

	private static boolean centerText() {
		Module module = Jello.getModule("ClickGUI");
		Value value = module == null ? null : module.getValue("TextPos");
		return !(value instanceof ModeValue mode) || mode.is("Center");
	}

	private static boolean blurEnabled() {
		Module menu = Jello.getModule("Menu");
		Module clickGui = Jello.getModule("ClickGUI");
		Value blur = menu == null ? null : menu.getValue("Blur");
		Value clickBlur = clickGui == null ? null : clickGui.getValue("Blur");
		return (blur instanceof BooleanValue b && b.getValue()) || (clickBlur instanceof BooleanValue c && c.getValue());
	}

	private static boolean particlesEnabled() {
		Module module = Jello.getModule("ClickGUI");
		Value value = module == null ? null : module.getValue("Particles");
		return !(value instanceof BooleanValue bool) || bool.getValue();
	}

	private static int panelBody() {
		return isDark() ? 0xF218191E : 0xF7FFFFFF;
	}

	private static int panelHeader() {
		return isDark() ? 0xE6323236 : 0xE69B9B9B;
	}

	private static int panelBorder() {
		return isDark() ? 0x2EFFFFFF : 0x40FFFFFF;
	}

	private static int accent() {
		return isDark() ? 0xFF4BA3FF : 0xFF2F8FF7;
	}

	private static int themeText(boolean toggled, float alpha) {
		int base = toggled ? 0xFFFFFFFF : (isDark() ? 0xFFF1F3F8 : 0xFF1B1D23);
		return applyAlpha(base, alpha);
	}

	private static int headerText(float alpha) {
		return applyAlpha(0xFFFFFFFF, alpha);
	}

	private static int mutedText(float alpha) {
		return applyAlpha(isDark() ? 0xFFA5ABBA : 0xFF5C6270, alpha);
	}

	private static int hoverRow() {
		return isDark() ? 0x18FFFFFF : 0x0C000000;
	}

	private static int applyAlpha(int color, float alpha) {
		int a = (int) (((color >>> 24) & 0xFF) * Math.max(0, Math.min(1, alpha)));
		return (a << 24) | (color & 0x00FFFFFF);
	}

	private float fitScale() {
		return Math.min(1.5f, Math.min((width - 8f) / VIRTUAL_W, (height - 8f) / VIRTUAL_H));
	}

	/** Same scale the panels are drawn with, so mouse mapping matches the intro animation. */
	private float renderScale() {
		float p = Anim.smooth(percent, lastPercent);
		float p2 = Anim.smooth(percent2, lastPercent2);
		return fitScale() * Math.min(1f, p > 0.98f ? p : p2);
	}

	private float offsetX(float scale) {
		return (width - VIRTUAL_W * scale) / 2f;
	}

	private float offsetY(float scale) {
		return (height - VIRTUAL_H * scale) / 2f;
	}

	private int toVirtualX(double screenX) {
		float scale = renderScale();
		return Math.round((float) ((screenX - offsetX(scale)) / scale));
	}

	private int toVirtualY(double screenY) {
		float scale = renderScale();
		return Math.round((float) ((screenY - offsetY(scale)) / scale));
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		this.mouseX = toVirtualX(mouseX);
		this.mouseY = toVirtualY(mouseY);
		scrollDelta = 0;
		// The intro starts at 1.23; clamping to 1 keeps the canvas inside small windows.
		float scale = renderScale();
		g.pose().pushMatrix();
		g.pose().translate(width / 2f, height / 2f);
		g.pose().scale(scale, scale);
		g.pose().translate(-VIRTUAL_W / 2f, -VIRTUAL_H / 2f);
		if (particlesEnabled()) {
			for (Particle particle : particles) {
				Render2D.circle(g, particle.x, particle.y, particle.size,
					Render2D.color(0.35f, 0.62f, 1f, 0.10f));
			}
		}
		drawPanels(g, this.mouseX, this.mouseY);
		if (selectedModule != null && selectedCategory != null && settingsTrans > 0.001f) {
			Render2D.rect(g, 0, 0, VIRTUAL_W, VIRTUAL_H,
				Render2D.color(0, 0, 0, 0.45f * Anim.smooth(settingsTrans, lastSettingsTrans)));
		}
		drawSettings(g, this.mouseX, this.mouseY);
		g.pose().popMatrix();
	}

	/** Closing animation, drawn by {@link com.mentalfrostbyte.jello.hud.JelloHud} once the screen is gone. */
	public void renderOutro(GuiGraphicsExtractor g) {
		if (!closing || outro <= 1.001f) {
			return;
		}
		float alpha = Math.max(0, Math.min(1, (1 - (outro - 1) - 0.4f) * 1.66667f));
		if (alpha <= 0.001f) {
			return;
		}
		float scale = fitScale();
		g.pose().pushMatrix();
		g.pose().translate(width / 2f, height / 2f);
		g.pose().scale(scale, scale);
		g.pose().translate(-VIRTUAL_W / 2f, -VIRTUAL_H / 2f);
		layoutPanels();
		for (Panel panel : panels) {
			panel.draw(g, -1000, -1000, alpha);
		}
		g.pose().popMatrix();
	}

	private void layoutPanels() {
		for (int i = 0; i < panels.size(); i++) {
			Panel panel = panels.get(i);
			if (!panel.dragging) {
				panel.x = 2 + (i % 4) * (PANEL_W + PANEL_GAP);
				panel.y = 2 + (i / 4) * 114;
			}
		}
	}

	private void drawPanels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		layoutPanels();
		for (Panel panel : panels) {
			if (panel.dragging) {
				panel.x = panel.lastX + mouseX - panel.startX;
				panel.y = panel.lastY + mouseY - panel.startY;
			}
			panel.draw(g, mouseX, mouseY, 1f);
		}
	}

	private int popupColumns() {
		return selectedModule != null && selectedModule.getValues().size() > 7 ? 2 : 1;
	}

	private int settingsPerPage() {
		return popupColumns() == 2 ? 14 : 7;
	}

	private int settingsPageCount() {
		if (selectedModule == null) {
			return 1;
		}
		return Math.max(1, (selectedModule.getValues().size() + settingsPerPage() - 1) / settingsPerPage());
	}

	private int settingsStart() {
		return Math.min(settingsPage * settingsPerPage(), selectedModule == null ? 0 : selectedModule.getValues().size());
	}

	private int settingsEnd() {
		return Math.min(settingsStart() + settingsPerPage(), selectedModule == null ? 0 : selectedModule.getValues().size());
	}

	private void changeSettingsPage(int delta) {
		settingsPage = Math.max(0, Math.min(settingsPageCount() - 1, settingsPage + delta));
	}

	private float popupWidth(float intro) {
		return popupColumns() == 2 ? 380 : 200;
	}

	private float popupX(float intro, float popupWidth) {
		return (VIRTUAL_W - popupWidth) / 2f;
	}

	private float popupY(float height) {
		return Math.max(0, (VIRTUAL_H - height) / 2f);
	}

	private void drawSettings(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		if (selectedCategory == null || selectedModule == null) {
			return;
		}
		float intro = Anim.smooth(settingsTrans, lastSettingsTrans);
		if (intro <= 0.001f) {
			return;
		}
		float width = popupWidth(intro);
		int columns = popupColumns();
		int total = Math.max(1, settingsEnd() - settingsStart());
		int rows = (total + columns - 1) / columns;
		float height = HEADER_H + 10 + rows * 17 + 8;
		float x = popupX(intro, width);
		float y = popupY(height);
		frame(g, x, y, width, height, intro);
		Fonts.jelloFontMedium12.drawString(g, selectedModule.getName(), x + 10, y + 6.5f, headerText(intro));
		if (settingsPageCount() > 1) {
			Fonts.jelloFontRegular.drawString(g, "< " + (settingsPage + 1) + "/" + settingsPageCount() + " >",
				x + width - 62, y + 8.5f, mutedText(intro));
		}

		int index = 0;
		List<Value> values = selectedModule.getValues();
		for (int vi = settingsStart(); vi < settingsEnd(); vi++) {
			Value value = values.get(vi);
			int column = index % columns;
			int row = index / columns;
			float colW = width / columns;
			float colX = x + column * colW;
			float rowY = y + HEADER_H + 10 + 17 * row;
			String label = Fonts.jelloFontGui.trimToWidth(value.getName(), colW - 102);
			if (value instanceof BooleanValue bool) {
				float bx = colX + colW - 19;
				Render2D.image(g, bool.getValue() ? "checked" : "unchecked", bx, rowY, 12, 12, Render2D.white(intro));
				Fonts.jelloFontRegular.drawString(g, label, colX + 7, rowY + 2.5f, themeText(false, intro));
			} else if (value instanceof ModeValue mode) {
				float bx = colX + colW - 96;
				Render2D.roundedRectBorder(g, bx, rowY, 89, 13, 4,
					applyAlpha(panelBorder(), intro), isDark() ? applyAlpha(0xFFFFFF, 0.08f * intro) : applyAlpha(0x000000, 0.06f * intro));
				Fonts.jelloFontRegular.drawString(g, mode.getMode(), bx + 5, rowY + 2.5f, themeText(false, intro));
				Fonts.jelloFontRegular.drawString(g, label, colX + 7, rowY + 2.5f, themeText(false, intro));
			} else if (value instanceof NumberValue number) {
				float sliderLeft = colX + colW - 60;
				float sliderWidth = 40;
				float actual = (float) ((number.getValue() - number.getMin()) / (number.getMax() - number.getMin()));
				Render2D.roundedRect(g, sliderLeft, rowY + 9, sliderWidth, 3, 1.5f,
					isDark() ? applyAlpha(0xFFFFFF, 0.14f * intro) : applyAlpha(0x000000, 0.12f * intro));
				Render2D.roundedRect(g, sliderLeft, rowY + 9, sliderWidth * actual, 3, 1.5f, applyAlpha(accent(), intro));
				float headX = sliderLeft + sliderWidth * actual;
				Render2D.circle(g, headX, rowY + 10.5f, 4.5f, applyAlpha(0xFFFFFFFF, intro));
				Render2D.circle(g, headX, rowY + 10.5f, 3.0f, applyAlpha(accent(), intro));
				Fonts.jelloFontRegular.drawString(g, label, colX + 7, rowY + 2.5f, themeText(false, intro));
			}
			index++;
		}
	}

	/** Rounded glass panel: soft shadow, 1px border, body and header band. */
	private static void frame(GuiGraphicsExtractor g, float x, float y, float w, float h, float alpha) {
		Render2D.roundedRect(g, x - 5, y - 3, w + 10, h + 10, 12, applyAlpha(0x000000, 0.30f * alpha));
		Render2D.roundedRectBorder(g, x - 1, y - 1, w + 2, h + 2, 9, applyAlpha(panelBorder(), alpha),
			applyAlpha(panelBody(), alpha));
		Render2D.roundedRect(g, x, y, w, HEADER_H, 8, applyAlpha(panelHeader(), alpha));
		Render2D.rect(g, x, y + HEADER_H - 8, x + w, y + HEADER_H, applyAlpha(panelHeader(), alpha));
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		mouseX = toVirtualX(event.x());
		mouseY = toVirtualY(event.y());
		selectedValue = null;
		layoutPanels();
		if (selectedModule != null && selectedCategory != null) {
			if (event.button() == com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT && handlePopupClick()) {
				return true;
			}
			// Clicking another panel while the popup is open closes it and still selects the clicked module.
			exit = true;
		}
		for (Panel panel : panels) {
			panel.mouseClicked(event.button(), this);
		}
		return true;
	}

	private boolean handlePopupClick() {
		float intro = Anim.smooth(settingsTrans, lastSettingsTrans);
		float width = popupWidth(intro);
		float x = popupX(intro, width);
		int columns = popupColumns();
		int total = Math.max(1, settingsEnd() - settingsStart());
		int rows = (total + columns - 1) / columns;
		float y = popupY(HEADER_H + 10 + rows * 17 + 8);
		if (settingsPageCount() > 1 && mouseY >= y && mouseY <= y + HEADER_H) {
			if (mouseX >= x + width - 70 && mouseX <= x + width - 34) {
				changeSettingsPage(-1);
				return true;
			}
			if (mouseX > x + width - 34 && mouseX <= x + width - 8) {
				changeSettingsPage(1);
				return true;
			}
		}
		int index = 0;
		List<Value> values = selectedModule.getValues();
		for (int vi = settingsStart(); vi < settingsEnd(); vi++) {
			Value value = values.get(vi);
			int column = index % columns;
			int row = index / columns;
			float colW = width / columns;
			float colX = x + column * colW;
			float rowY = y + HEADER_H + 10 + 17 * row;
			if (value instanceof BooleanValue bool) {
				if (Render2D.hovering(colX + colW - 19, rowY, 12, 12, mouseX, mouseY)) {
					bool.toggle();
					return true;
				}
			} else if (value instanceof ModeValue mode) {
				if (Render2D.hovering(colX + colW - 96, rowY, 89, 13, mouseX, mouseY)) {
					mode.cycle();
					return true;
				}
			} else if (value instanceof NumberValue number) {
				float sliderLeft = colX + colW - 60;
				float sliderWidth = 40;
				if (Render2D.hovering(sliderLeft - 4.5f, rowY - 4.5f, sliderWidth + 9, 21, mouseX, mouseY)) {
					float actual = Math.max(0, Math.min(1, (mouseX - sliderLeft) / sliderWidth));
					number.setVal(number.getMin() + actual * (number.getMax() - number.getMin()));
					selectedValue = number;
					return true;
				}
			}
			index++;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		selectedValue = null;
		for (Panel panel : panels) {
			panel.dragging = false;
		}
		return true;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		mouseX = toVirtualX(event.x());
		mouseY = toVirtualY(event.y());
		if (selectedValue != null) {
			float intro = Anim.smooth(settingsTrans, lastSettingsTrans);
			float width = popupWidth(intro);
			float x = popupX(intro, width);
			int index = selectedModule.getValues().indexOf(selectedValue);
			int columns = popupColumns();
			float colW = width / columns;
			float sliderLeft = x + (index % columns) * colW + colW - 60;
			float actual = Math.max(0, Math.min(1, (mouseX - sliderLeft) / 40.0f));
			selectedValue.setVal(selectedValue.getMin() + actual * (selectedValue.getMax() - selectedValue.getMin()));
			return true;
		}
		return super.mouseDragged(event, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		int vx = toVirtualX(x);
		int vy = toVirtualY(y);
		scrollDelta = (float) scrollY;
		if (selectedModule != null && settingsPageCount() > 1) {
			changeSettingsPage(scrollY > 0 ? 1 : -1);
			return true;
		}
		layoutPanels();
		for (Panel panel : panels) {
			if (Render2D.hovering(panel.x, panel.y, PANEL_W, PANEL_H, vx, vy)) {
				panel.onScroll((float) scrollY, vx, vy);
			}
		}
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.isEscape()) {
			if (selectedModule != null) {
				exit = true;
			} else {
				onClose();
			}
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public void onClose() {
		this.minecraft.gui.setScreen(null);
		this.wasOpen = false;
		this.closing = true;
	}

	public class Panel {
		public float x;
		public float y;
		public final Category category;
		public boolean dragging;
		public float startX;
		public float startY;
		public float lastX;
		public float lastY;
		public float scroll;
		private float lastScroll;
		private float scrollFade;

		Panel(float x, float y, Category category) {
			this.x = x;
			this.y = y;
			this.category = category;
		}

		public void onTick() {
			lastScroll = scroll;
			scrollFade = Math.max(0, scrollFade - 0.02f);
			for (Module module : Jello.getModulesInCategory(category)) {
				module.lastHoverPercent = module.hoverPercent;
				boolean hover = isHovering(module, mouseX, mouseY);
				if (hover && module.hoverPercent > 0) {
					module.hoverPercent = Math.max(0, module.hoverPercent - 0.25f);
				} else if (!hover && module.hoverPercent < 1) {
					module.hoverPercent = Math.min(1, module.hoverPercent + 0.25f);
				}
			}
		}

		boolean isHovering(Module module, int mx, int my) {
			List<Module> modules = Jello.getModulesInCategory(category);
			int index = modules.indexOf(module);
			if (index == -1) {
				return false;
			}
			return Render2D.hovering(x, y + HEADER_H + index * ROW_H - scroll, PANEL_W, ROW_H, mx, my)
				&& Render2D.hovering2(x, y + HEADER_H, x + PANEL_W, y + HEADER_H + LIST_H, mx, my);
		}

		public void onScroll(float delta, int mx, int my) {
			List<Module> modules = Jello.getModulesInCategory(category);
			float max = Math.max(0, modules.size() * ROW_H - LIST_H);
			if (!Render2D.hovering(x, y, PANEL_W, PANEL_H, mx, my) || max <= 0) {
				return;
			}
			scroll = Math.max(0, Math.min(max, Math.round((scroll - delta * 15f) / 15f) * 15f));
			scrollFade = 1;
		}

		public void mouseClicked(int button, JelloGui parent) {
			boolean left = button == com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT;
			boolean right = button == com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_RIGHT;
			if (left && Render2D.hovering(x, y, PANEL_W, HEADER_H, parent.mouseX, parent.mouseY)) {
				startX = parent.mouseX;
				startY = parent.mouseY;
				lastX = x;
				lastY = y;
				dragging = true;
			}
			List<Module> modules = Jello.getModulesInCategory(category);
			for (int i = 0; i < modules.size(); i++) {
				Module module = modules.get(i);
				if (!isHovering(module, parent.mouseX, parent.mouseY)) {
					continue;
				}
				if (left) {
					module.toggle();
				} else if (right) {
					parent.selectedModule = module;
					parent.selectedCategory = this;
					parent.exit = false;
					parent.settingsTrans = 0;
					parent.lastSettingsTrans = 0;
					parent.settingsPage = 0;
				}
				return;
			}
		}

		public void draw(GuiGraphicsExtractor g, int mouseX, int mouseY, float alpha) {
			frame(g, x, y, PANEL_W, PANEL_H, alpha);
			if (centerText()) {
				Fonts.jelloFontMedium12.drawCenteredString(g, category.name, x + PANEL_W / 2f, y + 6.5f, headerText(alpha));
			} else {
				Fonts.jelloFontMedium12.drawString(g, category.name, x + 10, y + 6.5f, headerText(alpha));
			}
			List<Module> modules = Jello.getModulesInCategory(category);
			Render2D.scissor(g, x + 2, y + HEADER_H, x + PANEL_W - 2, y + HEADER_H + LIST_H);
			for (int i = 0; i < modules.size(); i++) {
				Module module = modules.get(i);
				float rowY = y + HEADER_H + i * ROW_H - Anim.smooth(scroll, lastScroll) + 1;
				float hover = Anim.smooth(module.hoverPercent, module.lastHoverPercent);
				if (module.isToggled()) {
					Render2D.roundedRect(g, x + 3, rowY, PANEL_W - 6, ROW_H - 2, 4,
						applyAlpha(accent(), alpha * (0.82f + 0.18f * hover)));
				} else if (hover > 0.02f) {
					Render2D.roundedRect(g, x + 3, rowY, PANEL_W - 6, ROW_H - 2, 4, applyAlpha(hoverRow(), alpha * hover));
				}
				if (centerText()) {
					Fonts.jelloFontRegular.drawCenteredString(g, module.getName(), x + PANEL_W / 2f, rowY + 2.5f,
						themeText(module.isToggled(), alpha));
				} else {
					Fonts.jelloFontRegular.drawString(g, module.getName(), x + 10, rowY + 2.5f,
						themeText(module.isToggled(), alpha));
				}
			}
			g.disableScissor();
			if (scrollFade > 0.01f) {
				float max = Math.max(1, modules.size() * ROW_H - LIST_H);
				float barHeight = LIST_H * (LIST_H / (modules.size() * ROW_H + 1));
				float barY = y + HEADER_H + (LIST_H - barHeight) * (scroll / max);
				Render2D.roundedRect(g, x + PANEL_W - 4.5f, y + HEADER_H, 2, LIST_H, 1,
					isDark() ? applyAlpha(0xFFFFFF, 0.10f * scrollFade * alpha) : applyAlpha(0x000000, 0.10f * scrollFade * alpha));
				Render2D.roundedRect(g, x + PANEL_W - 4.5f, barY, 2, barHeight, 1, applyAlpha(accent(), 0.8f * scrollFade * alpha));
			}
		}
	}

	private static final class Particle {
		float x;
		float y;
		final float vx;
		final float vy;
		final float size;

		Particle(float x, float y, float vx, float vy, float size) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.size = size;
		}
	}
}
