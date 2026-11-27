package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;

/** Breaks the closest blocks inside the radius. */
public class Nuker extends Module {
	/** Meteor Client variant selector. */
	private final ModeValue source = addValue(new ModeValue("Source", "Sigma", "Sigma", "Meteor"));
	private final NumberValue range = addValue(new NumberValue("Range", 1, 1, 6, 1));

	public Nuker() {
		super("Nuker", 0, Category.WORLD);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		int r = (int) range.getValue();
		var origin = mc.player.blockPosition();
		for (int x = -r; x <= r; x++) {
			for (int y = -r; y <= r; y++) {
				for (int z = -r; z <= r; z++) {
					var pos = origin.offset(x, y, z);
					if (!mc.level.getBlockState(pos).isAir()) {
						mc.gameMode.destroyBlock(pos);
						return;
					}
				}
			}
		}
	}
}
