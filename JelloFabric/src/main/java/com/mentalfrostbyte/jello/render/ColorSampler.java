package com.mentalfrostbyte.jello.render;

import com.mentalfrostbyte.jello.Jello;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.textures.GpuTexture;
import net.minecraft.client.Minecraft;

/**
 * Reads single pixels of the main framebuffer asynchronously. Replaces the 1.8 glReadPixels calls JelloHud used
 * to tint the TabGUI and notification gradients with whatever is behind them.
 */
public final class ColorSampler {
	private final GpuBuffer[] buffers;
	private final int[] results;
	private final boolean[] pending;
	private boolean failed;

	public ColorSampler(int points) {
		buffers = new GpuBuffer[points];
		results = new int[points];
		pending = new boolean[points];
		for (int i = 0; i < points; i++) {
			results[i] = 0xFFFFFFFF;
		}
	}

	/** Latest colour (ARGB, opaque) sampled for the point. */
	public int get(int index) {
		return results[index];
	}

	/** Queues a copy of the framebuffer pixel under the given GUI coordinate. Must run on the render thread. */
	public void sample(int index, float guiX, float guiY) {
		if (failed || pending[index]) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		RenderTarget target = mc.gameRenderer.mainRenderTarget();
		GpuTexture texture = target.getColorTexture();
		if (texture == null) {
			return;
		}
		int scale = mc.getWindow().getGuiScale();
		int px = Math.max(0, Math.min(target.width - 1, (int) (guiX * scale)));
		int py = Math.max(0, Math.min(target.height - 1, (int) (guiY * scale)));
		// Texture rows start at the bottom, see Screenshot.takeScreenshot.
		int ty = target.height - 1 - py;
		try {
			int blockSize = texture.getFormat().blockSize();
			if (buffers[index] == null) {
				buffers[index] = RenderSystem.getDevice().createBuffer(() -> "Jello colour sample",
					GpuBuffer.USAGE_MAP_READ | GpuBuffer.USAGE_COPY_DST, blockSize);
			}
			GpuBuffer buffer = buffers[index];
			pending[index] = true;
			RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer(texture, buffer, 0L, () -> {
				try (GpuBufferSlice.MappedView read = buffer.map(true, false)) {
					int abgr = read.data().getInt(0);
					int r = abgr & 0xFF, g = (abgr >> 8) & 0xFF, b = (abgr >> 16) & 0xFF;
					results[index] = 0xFF000000 | (r << 16) | (g << 8) | b;
				} finally {
					pending[index] = false;
				}
			}, 0, px, ty, 1, 1);
		} catch (RuntimeException e) {
			// Some backends may not support region copies; fall back to plain white like a blank background.
			failed = true;
			pending[index] = false;
			Jello.LOGGER.warn("Framebuffer colour sampling unavailable, TabGUI will use a neutral gradient", e);
		}
	}
}
