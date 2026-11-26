package com.mentalfrostbyte.jello.hud;

import com.mentalfrostbyte.jello.render.Render2D;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;

/**
 * Rotating terrain minimap inside the Jello "MiniMap" frame. The 1.8 client embedded Xaero's Minimap source for
 * this; this is a self-contained replacement that draws the same kind of map (surface colours, height shading,
 * player arrow, other players) without bundling third-party code.
 */
public class MiniMapRenderer {
	private static final int SIZE = 96;
	private final Identifier id = Identifier.fromNamespaceAndPath("jello", "dynamic/minimap");
	private DynamicTexture texture;
	private int lastUpdateTick = -100;
	private int centerX, centerZ;

	private void ensureTexture() {
		if (texture == null) {
			texture = new DynamicTexture(() -> "jello minimap", SIZE, SIZE, true);
			Minecraft.getInstance().getTextureManager().register(id, texture);
		}
	}

	private void update(ClientLevel level, LocalPlayer player) {
		NativeImage img = texture.getPixels();
		if (img == null) {
			return;
		}
		centerX = player.getBlockX();
		centerZ = player.getBlockZ();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int half = SIZE / 2;
		int[] prevHeight = new int[SIZE];
		for (int z = 0; z < SIZE; z++) {
			for (int x = 0; x < SIZE; x++) {
				int wx = centerX - half + x, wz = centerZ - half + z;
				int argb = 0xFF000000;
				int h = level.getHeight(Heightmap.Types.WORLD_SURFACE, wx, wz) - 1;
				if (level.hasChunk(wx >> 4, wz >> 4) && h > level.getMinY()) {
					pos.set(wx, h, wz);
					BlockState state = level.getBlockState(pos);
					MapColor color = state.getMapColor(level, pos);
					MapColor.Brightness brightness = MapColor.Brightness.NORMAL;
					if (z > 0) {
						brightness = h > prevHeight[x] ? MapColor.Brightness.HIGH : h < prevHeight[x] ? MapColor.Brightness.LOW : MapColor.Brightness.NORMAL;
					}
					prevHeight[x] = h;
					if (color != MapColor.NONE) {
						argb = color.calculateARGBColor(brightness) | 0xFF000000;
					}
				}
				img.setPixel(x, z, argb);
			}
		}
		texture.upload();
	}

	/** Draws the map clipped to the given square. */
	public void render(GuiGraphicsExtractor g, float x, float y, float size) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		ClientLevel level = mc.level;
		if (player == null || level == null) {
			return;
		}
		ensureTexture();
		if (player.tickCount - lastUpdateTick >= 10 || player.tickCount < lastUpdateTick
			|| Math.abs(player.getBlockX() - centerX) > 8 || Math.abs(player.getBlockZ() - centerZ) > 8) {
			lastUpdateTick = player.tickCount;
			update(level, player);
		}
		float pt = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
		float yaw = player.getYRot(pt);
		float cx = x + size / 2, cy = y + size / 2;
		float pixel = size / (SIZE * 0.75f);
		double offX = player.getX() - (centerX + 0.5), offZ = player.getZ() - (centerZ + 0.5);
		Render2D.scissor(g, x, y, x + size, y + size);
		g.pose().pushMatrix();
		g.pose().translate(cx, cy);
		g.pose().rotate((float) Math.toRadians(180 - yaw));
		float drawn = SIZE * pixel;
		Render2D.image(g, id, (float) (-drawn / 2 - offX * pixel), (float) (-drawn / 2 - offZ * pixel), drawn, drawn, -1);
		for (Player other : level.players()) {
			if (other == player) {
				continue;
			}
			double dx = (other.getX() - player.getX()) * pixel, dz = (other.getZ() - player.getZ()) * pixel;
			Render2D.circle(g, (float) dx, (float) dz, 1.5f, 0xFFFF5555);
		}
		g.pose().popMatrix();
		g.disableScissor();
		// Player marker, always pointing up.
		Render2D.rect(g, cx - 0.5f, cy - 3, cx + 0.5f, cy + 1, 0xFFFFFFFF);
		Render2D.rect(g, cx - 1.5f, cy - 1, cx + 1.5f, cy, 0xFFFFFFFF);
	}
}
