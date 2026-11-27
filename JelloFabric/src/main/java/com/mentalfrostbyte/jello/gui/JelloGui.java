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
	private static final float PANEL_H = 150;
	private static final float BORDER = 9.5f;
	private static final float VIRTUAL_W = 490;
	private static final float VIRTUAL_H = 385;
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
		int index = 0;
		for (Category category : Category.values()) {
			float x = 10 + (index % 4) * (PANEL_W + PANEL_GAP);
			float y = 10 + (index / 4) * 190;
			panels.add(new Panel(x, y, category));
			index++;
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
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

	private static boolean particlesEnabled() {
		Module module = Jello.getModule("ClickGUI");
		Value value = module == null ? null : module.getValue("Particles");
		return !(value instanceof BooleanValue bool) || bool.getValue();
	}

	private static String panelTexture() {
		return isDark() ? "jellopanel_dark" : "jellopanelnew";
	}

	private static int themeText(boolean toggled, float alpha) {
		if (isDark()) {
			return Render2D.color(0.9f, 0.9f, 0.9f, alpha);
		}
		return toggled ? Render2D.color(1, 1, 1, alpha) : Render2D.color(0, 0, 0, alpha);
	}

	private static int headerText(float alpha) {
		return isDark() ? Render2D.color(1, 1, 1, alpha) : Render2D.color(0x6F6F6F / 255f, 0x6F6F6F / 255f, 0x6F6F6F / 255f, alpha);
	}

	private float fitScale() {
		return Math.min(1f, Math.min((width - 8f) / VIRTUAL_W, (height - 8f) / VIRTUAL_H));
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
		Module menu = Jello.getModule("Menu");
		Module clickGui = Jello.getModule("ClickGUI");
		Value blur = menu == null ? null : menu.getValue("Blur");
		Value clickBlur = clickGui == null ? null : clickGui.getValue("Blur");
		if ((blur instanceof BooleanValue b && b.getValue()) || (clickBlur instanceof BooleanValue c && c.getValue())) {
			try {
				g.blurBeforeThisStratum();
			} catch (IllegalStateException ignored) {
				// The vanilla background may already have queued the single blur allowed per frame.
			}
		}
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
		for (Panel panel : panels) {
			panel.draw(g, -1000, -1000, alpha);
		}
		g.pose().popMatrix();
	}

	private void drawPanels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
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

	private float popupWidth(float intro) {
		return popupColumns() == 2 ? 250 : PANEL_W + 60 * intro;
	}

	private float popupX(float intro, float popupWidth) {
		return Math.max(0, Math.min(selectedCategory.x - 30 * intro, VIRTUAL_W - popupWidth - 2));
	}

	private void drawSettings(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		if (selectedCategory == null || selectedModule == null) {
			return;
		}
		float intro = Anim.smooth(settingsTrans, lastSettingsTrans);
		if (intro <= 0.001f) {
			return;
		}
		Render2D.rect(g, 0, 0, VIRTUAL_W, VIRTUAL_H, Render2D.color(0, 0, 0, intro * 180 / 255f));
		float width = popupWidth(intro);
		int columns = popupColumns();
		int rows = (selectedModule.getValues().size() + columns - 1) / columns;
		float height = HEADER_H + 10 + rows * 17 + 8;
		float x = popupX(intro, width);
		float y = selectedCategory.y;
		frame(g, x, y, width, height, intro);
		Fonts.jelloFontAddAlt.drawString(g, selectedModule.getName(), x + 8, y + 8, Render2D.color(1f, 1f, 1f, intro));

		int index = 0;
		for (Value value : selectedModule.getValues()) {
			int column = index % columns;
			int row = index / columns;
			float colX = x + column * 112;
			float rowY = y + HEADER_H + 10 + 17 * row;
			if (value instanceof BooleanValue bool) {
				float bx = colX + 88;
				Render2D.image(g, bool.getValue() ? "checked" : "unchecked", bx, rowY, 12, 12, Render2D.white(intro));
				Fonts.jelloFontGui.drawString(g, bool.getName(), colX + 7, rowY + 3, themeText(false, intro));
			} else if (value instanceof ModeValue mode) {
				float bx = colX + 30;
				Render2D.rect(g, bx, rowY, colX + 92, rowY + 13,
					isDark() ? Render2D.color(1, 1, 1, intro * 0.10f) : Render2D.color(0, 0, 0, intro * 0.10f));
				Fonts.jelloFontGui.drawString(g, mode.getMode(), bx + 4, rowY + 3, themeText(false, intro));
				Fonts.jelloFontGui.drawString(g, mode.getName(), colX + 7, rowY + 3, themeText(false, intro));
			} else if (value instanceof NumberValue number) {
				float sliderLeft = colX + 11 + 60;
				float sliderWidth = 30;
				float actual = (float) ((number.getValue() - number.getMin()) / (number.getMax() - number.getMin()));
				Render2D.image(g, "leftslider", sliderLeft, rowY + 10, 2, 3, Render2D.white(intro));
				Render2D.rect(g, sliderLeft, rowY + 10, sliderLeft + sliderWidth * actual, rowY + 13,
					Render2D.color(0x3B / 255f, 0x99 / 255f, 0xFC / 255f, intro));
				Render2D.rect(g, sliderLeft + sliderWidth * actual, rowY + 10, sliderLeft + sliderWidth + 2, rowY + 13,
					Render2D.color(0, 0, 0, intro * 0.1f));
				float headX = sliderLeft + sliderWidth * actual - 4.5f;
				Render2D.image(g, "sliderhead", headX, rowY - 4.5f, 11.5f, 12.5f, Render2D.white(intro));
				Fonts.jelloFontGui.drawString(g, number.getName(), colX + 7, rowY + 3, themeText(false, intro));
			}
			index++;
		}
	}

	/** The 1.8 panel used JelloPanel.png (238x338, i.e. 119x169 at GUI scale) stretched around the body. */
	private static void frame(GuiGraphicsExtractor g, float x, float y, float w, float h, float alpha) {
		Render2D.image(g, panelTexture(), x - BORDER, y - BORDER, w + BORDER * 2, h + BORDER * 2, Render2D.white(alpha));
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		mouseX = toVirtualX(event.x());
		mouseY = toVirtualY(event.y());
		selectedValue = null;
		if (selectedModule != null && selectedCategory != null) {
			if (event.button() == com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT && handlePopupClick()) {
				return true;
			}
			exit = true;
			return true;
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
		float y = selectedCategory.y;
		int columns = popupColumns();
		int index = 0;
		for (Value value : selectedModule.getValues()) {
			int column = index % columns;
			int row = index / columns;
			float colX = x + column * 112;
			float rowY = y + HEADER_H + 10 + 17 * row;
			if (value instanceof BooleanValue bool) {
				if (Render2D.hovering(colX + 88, rowY, 12, 12, mouseX, mouseY)) {
					bool.toggle();
					return true;
				}
			} else if (value instanceof ModeValue mode) {
				if (Render2D.hovering(colX + 30, rowY, 62, 13, mouseX, mouseY)) {
					mode.cycle();
					return true;
				}
			} else if (value instanceof NumberValue number) {
				float sliderLeft = colX + 11 + 60;
				float sliderWidth = 30;
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
			float x = popupX(intro, popupWidth(intro));
			int index = selectedModule.getValues().indexOf(selectedValue);
			int column = index % popupColumns();
			float sliderLeft = x + column * 112 + 11 + 60;
			float actual = Math.max(0, Math.min(1, (mouseX - sliderLeft) / 30.0f));
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
		for (Panel panel : panels) {
			panel.onScroll((float) scrollY, vx, vy);
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
				&& Render2D.hovering2(x, y + HEADER_H, x + PANEL_W, y + HEADER_H + 110, mx, my);
		}

		public void onScroll(float delta, int mx, int my) {
			List<Module> modules = Jello.getModulesInCategory(category);
			float max = Math.max(0, modules.size() * ROW_H - 110);
			if (!Render2D.hovering(x, y, PANEL_W, PANEL_H, mx, my) || max <= 0) {
				return;
			}
			scroll = Math.max(0, Math.min(max, scroll - delta * 18.5f));
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
				}
				return;
			}
		}

		public void draw(GuiGraphicsExtractor g, int mouseX, int mouseY, float alpha) {
			frame(g, x, y, PANEL_W, PANEL_H, alpha);
			if (centerText()) {
				Fonts.jelloFontAddAlt.drawCenteredString(g, category.name, x + PANEL_W / 2f, y + 8, headerText(alpha));
			} else {
				Fonts.jelloFontAddAlt.drawString(g, category.name, x + 10, y + 8, headerText(alpha));
			}
			List<Module> modules = Jello.getModulesInCategory(category);
			Render2D.scissor(g, x, y + HEADER_H, x + PANEL_W, y + HEADER_H + 110);
			for (int i = 0; i < modules.size(); i++) {
				Module module = modules.get(i);
				float rowY = y + HEADER_H + i * ROW_H - Anim.smooth(scroll, lastScroll);
				float hover = Anim.smooth(module.hoverPercent, module.lastHoverPercent);
				int background = module.isToggled()
					? Render2D.color(42 / 255f, 165 / 255f, 255 / 255f, alpha * (200 + 55 * hover) / 255f)
					: (isDark() ? Render2D.color(30 / 255f, 30 / 255f, 30 / 255f, alpha)
						: Render2D.color(1f, 1f, 1f, alpha * (0.8f + 0.16f * hover)));
				Render2D.rect(g, x, rowY, x + PANEL_W, rowY + ROW_H, background);
				if (centerText()) {
					Fonts.jelloFontAddAlt3.drawCenteredString(g, module.getName(), x + PANEL_W / 2f, rowY + 3.5f,
						themeText(module.isToggled(), alpha));
				} else {
					Fonts.jelloFontAddAlt3.drawString(g, module.getName(), x + 10, rowY + 3.5f,
						themeText(module.isToggled(), alpha));
				}
			}
			g.disableScissor();
			if (scrollFade > 0.01f) {
				float max = Math.max(1, modules.size() * ROW_H - 110);
				float barHeight = 110 * (110 / (modules.size() * ROW_H + 1));
				float barY = y + HEADER_H + (110 - barHeight) * (scroll / max);
				Render2D.rect(g, x + PANEL_W - 5, y + HEADER_H, x + PANEL_W - 3.5f, y + HEADER_H + 110,
					isDark() ? Render2D.color(1, 1, 1, 0.10f * scrollFade * alpha) : Render2D.color(0, 0, 0, 0.10f * scrollFade * alpha));
				Render2D.rect(g, x + PANEL_W - 5, barY, x + PANEL_W - 3.5f, barY + barHeight,
					Render2D.color(0x3B / 255f, 0x99 / 255f, 0xFC / 255f, 0.8f * scrollFade * alpha));
			}
			Render2D.image(g, "jellopaneloverlay", x - BORDER, y - BORDER, PANEL_W + BORDER * 2, PANEL_H + BORDER * 2,
				Render2D.white(alpha));
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
