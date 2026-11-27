package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/**
 * Meteor Client Xray: renders only whitelisted ores, every other block gets {@code opacity} alpha.
 *
 * <p>This needs the block-model/chunk-render pipeline (Meteor injects into
 * {@code LevelRenderer}/{@code BlockRenderDispatcher} alpha handling and chunk occlusion). Jello has no
 * world render event and its mixin set is fixed for this port, so the settings are kept and the module is a
 * safe no-op until those hooks exist. BlockESP covers the "see the ores" use case in the meantime.</p>
 */
public class Xray extends Module {
	private final NumberValue opacity = addValue(new NumberValue("Opacity", 25, 0, 255, 5));
	private final ModeValue fluidOpacity = addValue(new ModeValue("Fluid Opacity", "Both", "None", "Water", "Lava", "Both"));
	private final BooleanValue exposedOnly = addValue(new BooleanValue("Exposed Only", false));

	public Xray() {
		super("Xray", 0, Category.RENDER);
	}

	public int opacity() {
		return (int) opacity.getValue();
	}

	public boolean exposedOnly() {
		return exposedOnly.getValue();
	}

	public boolean appliesToFluid(boolean water, boolean lava) {
		return switch (fluidOpacity.getMode()) {
			case "None" -> false;
			case "Water" -> water;
			case "Lava" -> lava;
			default -> water || lava;
		};
	}
}
