package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/**
 * Meteor Client WallHack: makes selected blocks translucent and disables chunk occlusion.
 *
 * <p>Requires block-render alpha injection plus the chunk-occlusion hook (Meteor's
 * {@code ChunkOcclusionEvent}). Neither exists in Jello's fixed mixin set, so this is a safe no-op that keeps
 * the module's settings for when those hooks are added.</p>
 */
public class WallHack extends Module {
	private final NumberValue opacity = addValue(new NumberValue("Opacity", 0, 0, 255, 5));
	private final BooleanValue occludeChunks = addValue(new BooleanValue("Occlude Chunks", false));

	public WallHack() {
		super("Wall Hack", 0, Category.RENDER);
	}

	public int opacity() {
		return (int) opacity.getValue();
	}

	public boolean occludeChunks() {
		return occludeChunks.getValue();
	}
}
