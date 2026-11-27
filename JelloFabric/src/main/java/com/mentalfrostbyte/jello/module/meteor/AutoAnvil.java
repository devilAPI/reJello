package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.ButtonBlock;

/** Meteor Client auto-anvil: drops anvils on nearby players to break their helmets. */
public class AutoAnvil extends Module {
	private final NumberValue targetRange = addValue(new NumberValue("Target Range", 4.0, 0.0, 5.0, 0.1));
	private final ModeValue priority = addValue(new ModeValue("Target Priority", "Lowest Health", "Lowest Health", "Lowest Distance"));
	private final NumberValue height = addValue(new NumberValue("Height", 2, 0, 5, 1));
	private final NumberValue delay = addValue(new NumberValue("Delay", 10, 0, 50, 1));
	private final BooleanValue placeAtFeet = addValue(new BooleanValue("Place At Feet", true));
	private final BooleanValue multiPlace = addValue(new BooleanValue("Multi Place", true));
	private final BooleanValue toggleOnBreak = addValue(new BooleanValue("Toggle On Break", false));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));

	private Player target;
	private int timer;

	public AutoAnvil() {
		super("Auto Anvil", 0, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		target = null;
		timer = 0;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (toggleOnBreak.getValue() && target != null && target.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
			setToggled(false);
			return;
		}

		if (target == null || target.isDeadOrDying() || target.isRemoved()
			|| target.distanceTo(mc.player) > targetRange.getValue()) {
			target = MeteorCombatHelper.findTarget(targetRange.getValue(), priority.is("Lowest Health"));
		}
		if (target == null) {
			return;
		}

		if (placeAtFeet.getValue()) {
			int plate = findPlateSlot();
			if (plate != -1) {
				MeteorCombatHelper.place(target.blockPosition(), plate, rotate.getValue(), true);
			}
		}

		if (timer++ < (int) delay.getValue()) {
			return;
		}
		timer = 0;

		int anvil = findAnvilSlot();
		if (anvil == -1) {
			return;
		}
		for (int i = (int) height.getValue(); i > 1; i--) {
			BlockPos pos = target.blockPosition().above(i + 1);
			boolean clear = true;
			for (int j = 1; j <= i; j++) {
				if (!mc.level.getBlockState(target.blockPosition().above(j)).canBeReplaced()) {
					clear = false;
					break;
				}
			}
			if (!clear) {
				continue;
			}
			if (MeteorCombatHelper.place(pos, anvil, rotate.getValue(), true) && !multiPlace.getValue()) {
				break;
			}
		}
	}

	private int findPlateSlot() {
		if (mc.player == null) {
			return -1;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (stack.getItem() instanceof BlockItem blockItem
				&& (blockItem.getBlock() instanceof BasePressurePlateBlock
					|| blockItem.getBlock() instanceof ButtonBlock)) {
				return i;
			}
		}
		return -1;
	}

	private int findAnvilSlot() {
		if (mc.player == null) {
			return -1;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AnvilBlock) {
				return i;
			}
		}
		return -1;
	}
}
