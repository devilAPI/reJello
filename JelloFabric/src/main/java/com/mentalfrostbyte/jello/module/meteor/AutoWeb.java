package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/** Meteor Client auto-web: drops cobwebs on other players. */
public class AutoWeb extends Module {
	private final NumberValue placeRange = addValue(new NumberValue("Place Range", 4.0, 0.0, 6.0, 0.1));
	private final NumberValue targetRange = addValue(new NumberValue("Target Range", 10.0, 0.0, 30.0, 0.1));
	private final BooleanValue predictMovement = addValue(new BooleanValue("Predict Movement", true));
	private final NumberValue ticksToPredict = addValue(new NumberValue("Ticks To Predict", 10.0, 1.0, 30.0, 0.1));
	private final BooleanValue doubles = addValue(new BooleanValue("Doubles", false));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));

	public AutoWeb() {
		super("Auto Web", 0, Category.COMBAT);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		Player target = MeteorCombatHelper.findTarget(targetRange.getValue(), false);
		if (target == null) {
			return;
		}
		int slot = MeteorCombatHelper.findItemSlot(Items.COBWEB, true);
		if (slot == -1) {
			return;
		}

		Vec3 pos = target.position();
		if (predictMovement.getValue()) {
			double dx = target.getX() - target.xo;
			double dy = target.getY() - target.yo;
			double dz = target.getZ() - target.zo;
			pos = pos.add(dx * ticksToPredict.getValue(), dy * ticksToPredict.getValue(), dz * ticksToPredict.getValue());
		}

		BlockPos base = BlockPos.containing(pos);
		if (canPlaceWebAt(base)) {
			MeteorCombatHelper.place(base, slot, rotate.getValue(), true);
		}
		if (doubles.getValue() && canPlaceWebAt(base.above())) {
			MeteorCombatHelper.place(base.above(), slot, rotate.getValue(), true);
		}
	}

	private boolean canPlaceWebAt(BlockPos pos) {
		return MeteorCombatHelper.isReplaceable(pos) && MeteorCombatHelper.withinReach(pos, placeRange.getValue());
	}
}
