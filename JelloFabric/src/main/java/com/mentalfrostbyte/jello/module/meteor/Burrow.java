package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Meteor Client burrow: clips the player into a block at their feet. */
public class Burrow extends Module {
	private final ModeValue blockMode = addValue(new ModeValue("Block To Use", "EChest",
		"EChest", "Obsidian", "Anvil", "Held"));
	private final BooleanValue instant = addValue(new BooleanValue("Instant", true));
	private final BooleanValue automatic = addValue(new BooleanValue("Automatic", true));
	private final NumberValue triggerHeight = addValue(new NumberValue("Trigger Height", 1.12, 0.01, 1.4, 0.01));
	private final NumberValue rubberbandHeight = addValue(new NumberValue("Rubberband Height", 12.0, 0.0, 30.0, 0.5));
	private final BooleanValue onlyInHoles = addValue(new BooleanValue("Only In Holes", false));
	private final BooleanValue center = addValue(new BooleanValue("Center", true));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));

	private BlockPos burrowPos;
	private boolean shouldBurrow;
	private int step;

	public Burrow() {
		super("Burrow", 0, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		step = 0;
		shouldBurrow = false;
		if (mc.player == null || mc.level == null) {
			return;
		}
		if (!mc.level.getBlockState(mc.player.blockPosition()).canBeReplaced()) {
			setToggled(false);
			return;
		}
		if (onlyInHoles.getValue() && !MeteorCombatHelper.inHole(mc.player.blockPosition())) {
			setToggled(false);
			return;
		}
		if (!checkHead()) {
			setToggled(false);
			return;
		}
		if (findBlockSlot() == -1) {
			setToggled(false);
			return;
		}

		if (center.getValue()) {
			MeteorCombatHelper.centerPlayer();
		}
		burrowPos = mc.player.blockPosition();

		if (automatic.getValue()) {
			if (instant.getValue()) {
				shouldBurrow = true;
			} else {
				mc.player.jumpFromGround();
			}
		}
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || burrowPos == null) {
			return;
		}

		if (!instant.getValue()) {
			shouldBurrow = mc.player.getY() > burrowPos.getY() + triggerHeight.getValue();
		} else if (!shouldBurrow) {
			burrowPos = mc.player.blockPosition();
		}

		if (!shouldBurrow) {
			return;
		}

		if (!instant.getValue()) {
			placeBlock();
			if (mc.player != null) {
				mc.player.absSnapTo(mc.player.getX(), mc.player.getY() + rubberbandHeight.getValue(), mc.player.getZ());
			}
			setToggled(false);
			return;
		}

		// One position packet per tick: 26.3 disconnects on a second position packet in the same tick.
		switch (step++) {
			case 0 -> sendPos(burrowPos.getY() + 0.4);
			case 1 -> sendPos(burrowPos.getY() + 0.75);
			case 2 -> sendPos(burrowPos.getY() + 1.01);
			case 3 -> sendPos(burrowPos.getY() + 1.15);
			case 4 -> placeBlock();
			case 5 -> {
				sendPos(burrowPos.getY() + rubberbandHeight.getValue());
				setToggled(false);
			}
			default -> setToggled(false);
		}
	}

	private void sendPos(double y) {
		if (mc.getConnection() == null || mc.player == null) {
			return;
		}
		mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(), y, mc.player.getZ(),
			false, mc.player.horizontalCollision));
	}

	private void placeBlock() {
		if (mc.player == null || mc.gameMode == null || burrowPos == null) {
			return;
		}
		int slot = findBlockSlot();
		if (slot == -1) {
			setToggled(false);
			return;
		}
		MeteorCombatHelper.selectSlot(slot);
		Vec3 hit = Vec3.atCenterOf(burrowPos);
		if (rotate.getValue()) {
			float[] rotations = MeteorCombatHelper.rotationsTo(hit);
			mc.player.setYRot(rotations[0]);
			mc.player.setXRot(rotations[1]);
		}
		mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND,
			new BlockHitResult(hit, Direction.UP, burrowPos, false));
		mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
	}

	private int findBlockSlot() {
		if (mc.player == null) {
			return -1;
		}
		return switch (blockMode.getMode()) {
			case "Obsidian" -> {
				int slot = MeteorCombatHelper.findItemSlot(Items.OBSIDIAN, true);
				yield slot != -1 ? slot : MeteorCombatHelper.findItemSlot(Items.CRYING_OBSIDIAN, true);
			}
			case "Anvil" -> {
				int found = -1;
				for (int i = 0; i < 9; i++) {
					ItemStack stack = mc.player.getInventory().getItem(i);
					if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AnvilBlock) {
						found = i;
						break;
					}
				}
				yield found;
			}
			case "Held" -> mc.player.getMainHandItem().getItem() instanceof BlockItem
				? mc.player.getInventory().getSelectedSlot() : -1;
			default -> MeteorCombatHelper.findItemSlot(Items.ENDER_CHEST, true);
		};
	}

	private boolean checkHead() {
		if (mc.level == null || mc.player == null) {
			return false;
		}
		for (double x : new double[] {0.3, -0.3}) {
			for (double z : new double[] {0.3, -0.3}) {
				BlockPos pos = BlockPos.containing(mc.player.getX() + x, mc.player.getY() + 2.3, mc.player.getZ() + z);
				if (!mc.level.getBlockState(pos).canBeReplaced()) {
					return false;
				}
			}
		}
		return true;
	}
}
