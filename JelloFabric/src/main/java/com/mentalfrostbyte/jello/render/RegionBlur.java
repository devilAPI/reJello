package com.mentalfrostbyte.jello.render;

import com.mentalfrostbyte.jello.Jello;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;

/**
 * A tiny, linearly-filtered snapshot of the framebuffer used as a frosted backdrop for HUD panels.
 * Vanilla's blur pass is full-screen only, so this samples a small grid of the region and upscales it
 * with a linear sampler to get the same glass look inside the TabGUI.
 */
public final class RegionBlur {
	private static final int SAMPLES = 16;
	private final Identifier id;
	private LinearTexture texture;
	private boolean registered;
	private GpuBuffer buffer;
	private int bufferSize;
	private boolean pending;
	private boolean failed;
	private long lastSample;

	public RegionBlur(String name) {
		this.id = Identifier.fromNamespaceAndPath("jello", "dynamic/" + name);
	}

	private void ensure() {
		if (registered) {
			return;
		}
		registered = true;
		texture = new LinearTexture(id.getPath());
		Minecraft.getInstance().getTextureManager().register(id, texture);
	}

	/** Samples a GUI-space rectangle into the 16x16 texture. Safe to call every frame; it throttles itself. */
	public void sample(float guiX, float guiY, float guiW, float guiH) {
		long now = System.currentTimeMillis();
		if (failed || pending || now - lastSample < 250) {
			return;
		}
		lastSample = now;
		ensure();
		Minecraft mc = Minecraft.getInstance();
		var target = mc.gameRenderer.mainRenderTarget();
		GpuTexture source = target.getColorTexture();
		if (source == null) {
			return;
		}
		int scale = Math.max(1, mc.getWindow().getGuiScale());
		int w = Math.max(1, Math.min(target.width, (int) (guiW * scale)));
		int h = Math.max(1, Math.min(target.height, (int) (guiH * scale)));
		int px = Math.max(0, Math.min(target.width - w, (int) (guiX * scale)));
		int top = Math.max(0, Math.min(target.height - h, (int) (guiY * scale)));
		int ty = target.height - top - h;
		int size = w * h * source.getFormat().blockSize();
		try {
			if (buffer == null || bufferSize < size) {
				buffer = RenderSystem.getDevice().createBuffer(() -> "Jello HUD blur", GpuBuffer.USAGE_MAP_READ | GpuBuffer.USAGE_COPY_DST, size);
				bufferSize = size;
			}
			GpuBuffer readback = buffer;
			pending = true;
			RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer(source, readback, 0L, () -> {
				try (GpuBufferSlice.MappedView view = readback.map(true, false)) {
					NativeImage image = texture.pixels();
					int[] red = new int[SAMPLES * SAMPLES];
					int[] green = new int[SAMPLES * SAMPLES];
					int[] blue = new int[SAMPLES * SAMPLES];
					int[] count = new int[SAMPLES * SAMPLES];
					for (int y = 0; y < h; y++) {
						int sy = Math.min(SAMPLES - 1, y * SAMPLES / h);
						for (int x = 0; x < w; x++) {
							int sx = Math.min(SAMPLES - 1, x * SAMPLES / w);
							int abgr = view.data().getInt((y * w + x) * 4);
							int index = sy * SAMPLES + sx;
							red[index] += abgr & 0xFF;
							green[index] += (abgr >> 8) & 0xFF;
							blue[index] += (abgr >> 16) & 0xFF;
							count[index]++;
						}
					}
					for (int y = 0; y < SAMPLES; y++) {
						for (int x = 0; x < SAMPLES; x++) {
							int i = y * SAMPLES + x;
							int n = Math.max(1, count[i]);
							image.setPixel(x, y, Render2D.color(red[i] / n, green[i] / n, blue[i] / n, 255));
						}
					}
					texture.upload();
				} finally {
					pending = false;
				}
			}, 0, px, ty, w, h);
		} catch (RuntimeException e) {
			failed = true;
			pending = false;
			Jello.LOGGER.warn("HUD region blur unavailable", e);
		}
	}

	public Identifier id() {
		return id;
	}

	/** DynamicTexture hardcodes nearest filtering; this variant uses linear so the 16x16 grid looks blurred. */
	private static final class LinearTexture extends AbstractTexture {
		private final NativeImage pixels = new NativeImage(SAMPLES, SAMPLES, false);
		private final String label;

		LinearTexture(String label) {
			this.label = label;
			GpuDevice device = RenderSystem.getDevice();
			texture = device.createTexture(() -> "Jello HUD blur " + label, 5,
				com.mojang.renderpearl.api.GpuFormat.RGBA8_UNORM, SAMPLES, SAMPLES, 1, 1);
			textureView = device.createTextureView(texture);
			sampler = RenderSystem.getSamplerCache().getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE,
				FilterMode.LINEAR, FilterMode.LINEAR, false);
		}

		NativeImage pixels() {
			return pixels;
		}

		void upload() {
			RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, pixels);
		}
	}
}
