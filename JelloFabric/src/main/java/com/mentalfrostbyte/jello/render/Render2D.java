package com.mentalfrostbyte.jello.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

/**
 * Float-precision 2D drawing on top of GuiGraphicsExtractor (which only takes ints): every primitive is a
 * unit quad scaled through the pose matrix. Replaces Gui.drawFloatRect, drawModalRectWithCustomSizedTexture,
 * RenderingUtil circles and GlStateManager.color tinting from the 1.8 client.
 */
public final class Render2D {
	private static final int UV_PRECISION = 1000;
	private static Identifier circle;

	private Render2D() {
	}

	public static void rect(GuiGraphicsExtractor g, float x0, float y0, float x1, float y1, int color) {
		if ((color >>> 24) == 0) {
			return;
		}
		float minX = Math.min(x0, x1), minY = Math.min(y0, y1);
		float w = Math.abs(x1 - x0), h = Math.abs(y1 - y0);
		if (w <= 0 || h <= 0) {
			return;
		}
		g.pose().pushMatrix();
		g.pose().translate(minX, minY);
		g.pose().scale(w, h);
		g.fill(0, 0, 1, 1, color);
		g.pose().popMatrix();
	}

	/** Vertical gradient (top colour to bottom colour), like Gui.drawGradientRect. */
	public static void gradient(GuiGraphicsExtractor g, float x0, float y0, float x1, float y1, int top, int bottom) {
		float w = x1 - x0, h = y1 - y0;
		if (w <= 0 || h <= 0) {
			return;
		}
		g.pose().pushMatrix();
		g.pose().translate(x0, y0);
		g.pose().scale(w / 100f, h / 100f);
		g.fillGradient(0, 0, 100, 100, top, bottom);
		g.pose().popMatrix();
	}

	/** The whole texture stretched over the rectangle. */
	public static void image(GuiGraphicsExtractor g, String name, float x, float y, float w, float h) {
		image(g, Textures.get(name), x, y, w, h, -1);
	}

	public static void image(GuiGraphicsExtractor g, String name, float x, float y, float w, float h, int color) {
		image(g, Textures.get(name), x, y, w, h, color);
	}

	public static void image(GuiGraphicsExtractor g, Identifier tex, float x, float y, float w, float h, int color) {
		imageUV(g, tex, x, y, w, h, 0, 0, 1, 1, 1, 1, color);
	}

	/**
	 * Same semantics as 1.8 drawModalRectWithCustomSizedTexture(x, y, u, v, regionW, regionH, texW, texH)
	 * except the drawn size can differ from the region (w, h).
	 */
	public static void imageUV(GuiGraphicsExtractor g, String name, float x, float y, float w, float h, float u, float v,
		float regionW, float regionH, float texW, float texH, int color) {
		imageUV(g, Textures.get(name), x, y, w, h, u, v, regionW, regionH, texW, texH, color);
	}

	public static void imageUV(GuiGraphicsExtractor g, Identifier tex, float x, float y, float w, float h, float u, float v,
		float regionW, float regionH, float texW, float texH, int color) {
		if (w <= 0 || h <= 0 || (color >>> 24) == 0) {
			return;
		}
		g.pose().pushMatrix();
		g.pose().translate(x, y);
		g.pose().scale(w / UV_PRECISION, h / UV_PRECISION);
		// UV = u/texW .. (u+regionW)/texW, expressed in ints by scaling all virtual texture units by UV_PRECISION.
		g.blit(RenderPipelines.GUI_TEXTURED, tex, 0, 0, u * UV_PRECISION, v * UV_PRECISION, UV_PRECISION, UV_PRECISION,
			Math.round(regionW * UV_PRECISION), Math.round(regionH * UV_PRECISION),
			Math.round(texW * UV_PRECISION), Math.round(texH * UV_PRECISION), color);
		g.pose().popMatrix();
	}

	/** Thin line from (x0,y0) to (x1,y1). */
	public static void line(GuiGraphicsExtractor g, float x0, float y0, float x1, float y1, float thickness, int color) {
		float dx = x1 - x0, dy = y1 - y0;
		float length = (float) Math.sqrt(dx * dx + dy * dy);
		if (length <= 0 || thickness <= 0 || (color >>> 24) == 0) {
			return;
		}
		g.pose().pushMatrix();
		g.pose().translate(x0, y0);
		g.pose().rotate((float) Math.atan2(dy, dx));
		g.pose().scale(length, thickness);
		g.fill(0, 0, 1, 1, color);
		g.pose().popMatrix();
	}

	public static void circle(GuiGraphicsExtractor g, float cx, float cy, float radius, int color) {
		if (radius <= 0) {
			return;
		}
		image(g, circleTexture(), cx - radius, cy - radius, radius * 2, radius * 2, color);
	}

	private static Identifier circleTexture() {
		if (circle == null) {
			int size = 128;
			NativeImage img = new NativeImage(size, size, false);
			float r = size / 2f;
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					float dx = x + 0.5f - r, dy = y + 0.5f - r;
					float d = (float) Math.sqrt(dx * dx + dy * dy);
					float a = Math.max(0, Math.min(1, r - d));
					img.setPixel(x, y, ARGB.color((int) (a * 255), 255, 255, 255));
				}
			}
			circle = Identifier.fromNamespaceAndPath("jello", "dynamic/circle");
			Minecraft.getInstance().getTextureManager().register(circle, new DynamicTexture(() -> "jello circle", img));
		}
		return circle;
	}

	public static int color(float r, float g, float b, float a) {
		return ARGB.colorFromFloat(clamp(a), clamp(r), clamp(g), clamp(b));
	}

	public static int color(int r, int g, int b, int a) {
		return ARGB.color(Math.max(0, Math.min(255, a)), Math.max(0, Math.min(255, r)), Math.max(0, Math.min(255, g)), Math.max(0, Math.min(255, b)));
	}

	/** White with the given alpha; the 1.8 code's GlStateManager.color(1,1,1,a). */
	public static int white(float alpha) {
		return color(1f, 1f, 1f, alpha);
	}

	private static float clamp(float f) {
		return Math.max(0, Math.min(1, f));
	}

	public static boolean hovering(float x, float y, float w, float h, double mx, double my) {
		return mx >= x && my >= y && mx <= x + w && my <= y + h - 0.5f;
	}

	public static boolean hovering2(float x, float y, float x2, float y2, double mx, double my) {
		return mx >= x && my >= y && mx <= x2 && my <= y2;
	}

	public static void scissor(GuiGraphicsExtractor g, float x0, float y0, float x1, float y1) {
		g.enableScissor((int) Math.floor(x0), (int) Math.floor(y0), (int) Math.ceil(x1), (int) Math.ceil(y1));
	}
}
