package com.mentalfrostbyte.jello.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

/**
 * Replacement for the original AWT-rasterised CFont/JelloFontRenderer. Glyphs come from vanilla's TTF
 * provider (assets/jello/font/*.json); this class only adds float positioning and the old API names.
 */
public final class JFont {
	private final FontDescription.Resource description;
	private final Style style;
	private final float size;

	JFont(String id, float size) {
		this.description = new FontDescription.Resource(Identifier.fromNamespaceAndPath("jello", id));
		this.style = Style.EMPTY.withFont(description);
		this.size = size;
	}

	private static Font mc() {
		return Minecraft.getInstance().font;
	}

	public Component component(String text) {
		return Component.literal(text).withStyle(style);
	}

	public float getStringWidth(String text) {
		return mc().width(component(text));
	}

	public float getHeight() {
		return size;
	}

	/** Draws with the top of the text at {@code y}; colour is ARGB (alpha 0 draws nothing). */
	public float drawString(GuiGraphicsExtractor g, String text, float x, float y, int color) {
		if ((color >>> 24) == 0 || text.isEmpty()) {
			return x;
		}
		g.pose().pushMatrix();
		// Integer alignment keeps the TTF atlas crisp; fractional offsets made glyphs look blocky.
		g.pose().translate(Math.round(x), Math.round(y));
		g.text(mc(), component(text), 0, 0, color, false);
		g.pose().popMatrix();
		return x + getStringWidth(text);
	}

	/** Same as {@link #drawString} but with the vanilla drop shadow, for text over busy backgrounds. */
	public float drawStringShadow(GuiGraphicsExtractor g, String text, float x, float y, int color) {
		if ((color >>> 24) == 0 || text.isEmpty()) {
			return x;
		}
		g.pose().pushMatrix();
		g.pose().translate(Math.round(x), Math.round(y));
		g.text(mc(), component(text), 0, 0, color, true);
		g.pose().popMatrix();
		return x + getStringWidth(text);
	}

	public float drawCenteredShadow(GuiGraphicsExtractor g, String text, float x, float y, int color) {
		return drawStringShadow(g, text, x - getStringWidth(text) / 2f, y, color);
	}

	public float drawNoBSString(GuiGraphicsExtractor g, String text, float x, float y, int color) {
		return drawString(g, text, x, y, color);
	}

	public float drawCenteredString(GuiGraphicsExtractor g, String text, float x, float y, int color) {
		return drawString(g, text, x - getStringWidth(text) / 2f, y, color);
	}

	public float drawNoBSCenteredString(GuiGraphicsExtractor g, String text, float x, float y, int color) {
		return drawCenteredString(g, text, x, y, color);
	}

	public String trimToWidth(String text, float width) {
		return mc().substrByWidth(component(text), Math.round(width)).getString();
	}
}
