package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;

/**
 * Meteor Client NoRender: disables assorted overlays and animations.
 *
 * <p>Ported without mixins where the vanilla options allow it: weather (rain/thunder level), vignette and the
 * screen distortion used by nausea and the portal overlay ({@code screenEffectScale}). The remaining toggles
 * (pumpkin/fire/liquid overlays, HUD bars, fog, entity rendering, nametags, ...) are rendered by vanilla
 * methods that Jello does not inject into, so they are kept as settings but are inert for now.</p>
 */
public class NoRender extends Module {
	private final BooleanValue portalOverlay = addValue(new BooleanValue("Portal Overlay", false));
	private final BooleanValue nausea = addValue(new BooleanValue("Nausea", false));
	private final BooleanValue pumpkinOverlay = addValue(new BooleanValue("Pumpkin Overlay", false));
	private final BooleanValue fireOverlay = addValue(new BooleanValue("Fire Overlay", false));
	private final BooleanValue liquidOverlay = addValue(new BooleanValue("Liquid Overlay", false));
	private final BooleanValue vignette = addValue(new BooleanValue("Vignette", false));
	private final BooleanValue weather = addValue(new BooleanValue("Weather", false));
	private final BooleanValue fog = addValue(new BooleanValue("Fog", false));
	private final BooleanValue crosshair = addValue(new BooleanValue("Crosshair", false));
	private final BooleanValue bossBar = addValue(new BooleanValue("Boss Bar", false));
	private final BooleanValue scoreboard = addValue(new BooleanValue("Scoreboard", false));
	private final BooleanValue title = addValue(new BooleanValue("Title", false));
	private final BooleanValue heldItemName = addValue(new BooleanValue("Held Item Name", false));
	private final BooleanValue potionIcons = addValue(new BooleanValue("Potion Icons", false));
	private final BooleanValue armor = addValue(new BooleanValue("Armor", false));
	private final BooleanValue invisibility = addValue(new BooleanValue("Invisibility", false));
	private final BooleanValue glowing = addValue(new BooleanValue("Glowing", false));
	private final BooleanValue nametags = addValue(new BooleanValue("Nametags", false));
	private final BooleanValue signText = addValue(new BooleanValue("Sign Text", false));
	private final BooleanValue beaconBeams = addValue(new BooleanValue("Beacon Beams", false));

	private Boolean previousVignette;
	private double previousScreenEffect = 1.0;
	private boolean captured;

	public NoRender() {
		super("No Render", 0, Category.RENDER);
	}

	@Override
	public void onEnable() {
		captureOptions();
	}

	@Override
	public void onDisable() {
		if (!captured || mc.options == null) {
			return;
		}
		if (previousVignette != null) {
			mc.options.vignette().set(previousVignette);
		}
		mc.options.screenEffectScale().set(previousScreenEffect);
		captured = false;
	}

	@Override
	public void onUpdate() {
		if (!isToggled()) {
			return;
		}
		if (!captured) {
			captureOptions();
		}

		if (weather.getValue() && mc.level != null) {
			mc.level.setRainLevel(0f);
			mc.level.setThunderLevel(0f);
		}

		if (mc.options != null) {
			if (vignette.getValue()) {
				mc.options.vignette().set(false);
			} else if (previousVignette != null) {
				mc.options.vignette().set(previousVignette);
			}

			if (nausea.getValue() || portalOverlay.getValue()) {
				mc.options.screenEffectScale().set(0.0);
			} else {
				mc.options.screenEffectScale().set(previousScreenEffect);
			}
		}
	}

	private void captureOptions() {
		if (mc.options == null) {
			return;
		}
		previousVignette = mc.options.vignette().get();
		previousScreenEffect = mc.options.screenEffectScale().get();
		captured = true;
	}
}
