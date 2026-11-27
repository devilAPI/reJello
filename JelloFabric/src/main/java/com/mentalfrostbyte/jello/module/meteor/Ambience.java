package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/**
 * Meteor Client Ambience: recolours sky, clouds, lightning, grass/foliage, water/lava and fog.
 *
 * <p>All of these are read from vanilla's environment attributes / biome colour resolvers, which Meteor
 * patches with mixins (and with {@code SettingColor}s). Jello has no colour settings or those hooks, so the
 * toggles are ported as a no-op placeholder; wiring them needs EnvironmentAttribute, BiomeColors and fog
 * mixins plus a ColorValue setting type.</p>
 */
public class Ambience extends Module {
	private final BooleanValue endSky = addValue(new BooleanValue("End Sky", false));
	private final BooleanValue customSkyColor = addValue(new BooleanValue("Custom Sky Color", false));
	private final BooleanValue customCloudColor = addValue(new BooleanValue("Custom Cloud Color", false));
	private final BooleanValue customLightningColor = addValue(new BooleanValue("Custom Lightning Color", false));
	private final BooleanValue customGrassColor = addValue(new BooleanValue("Custom Grass Color", false));
	private final BooleanValue customFoliageColor = addValue(new BooleanValue("Custom Foliage Color", false));
	private final BooleanValue customWaterColor = addValue(new BooleanValue("Custom Water Color", false));
	private final BooleanValue customLavaColor = addValue(new BooleanValue("Custom Lava Color", false));
	private final BooleanValue customFogColor = addValue(new BooleanValue("Custom Fog Color", false));

	public Ambience() {
		super("Ambience", 0, Category.WORLD);
	}
}
