package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

/** Meteor Client self-web: places cobwebs on the local player. */
public class SelfWeb extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Normal", "Normal", "Smart"));
	private final NumberValue range = addValue(new NumberValue("Range", 3, 1, 7, 1));
	private final BooleanValue doublePlace = addValue(new BooleanValue("Double Place", false));
	private final BooleanValue autoToggle = addValue(new BooleanValue("Auto Toggle", true));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));

	public SelfWeb() {
		super("Self Web", 0, Category.COMBAT);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (mode.is("Smart")) {
			Player target = MeteorCombatHelper.findTarget(range.getValue(), false);
			if (target == null) {
				return;
			}
		}

		int slot = MeteorCombatHelper.findItemSlot(Items.COBWEB, true);
		if (slot == -1) {
			return;
		}

		boolean placed = MeteorCombatHelper.place(mc.player.blockPosition(), slot, rotate.getValue(), true);
		if (doublePlace.getValue()) {
			placed |= MeteorCombatHelper.place(mc.player.blockPosition().above(), slot, rotate.getValue(), true);
		}
		if (placed && autoToggle.getValue()) {
			setToggled(false);
		}
	}
}
