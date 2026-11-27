package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Meteor Client anchor: pulls you into the hole you are standing over and can stop your jump. */
public class Anchor extends Module {
	private final NumberValue maxHeight = addValue(new NumberValue("Max Height", 10, 0, 20, 1));
	private final NumberValue minPitch = addValue(new NumberValue("Min Pitch", 0, -90, 90, 1));
	private final BooleanValue cancelMove = addValue(new BooleanValue("Cancel Jump In Hole", false));
	private final BooleanValue pull = addValue(new BooleanValue("Pull", false));
	private final NumberValue pullSpeed = addValue(new NumberValue("Pull Speed", 0.3, 0.0, 5.0, 0.1));

	private final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
	private boolean wasInHole;
	private int holeX, holeZ;

	public boolean cancelJump;
	public boolean controlMovement;
	public double deltaX, deltaZ;

	public Anchor() {
		super("Anchor", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		wasInHole = false;
		holeX = 0;
		holeZ = 0;
		cancelJump = false;
		controlMovement = false;
	}

	@Override
	public void onDisable() {
		cancelJump = false;
		controlMovement = false;
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.level == null) {
			return;
		}

		int x = Mth.floor(p.getX());
		int y = Mth.floor(p.getY());
		int z = Mth.floor(p.getZ());

		if (isHole(x, y, z)) {
			wasInHole = true;
			holeX = x;
			holeZ = z;
			controlMovement = false;
			cancelJump = false;
			return;
		}

		if (wasInHole && holeX == x && holeZ == z) {
			return;
		} else if (wasInHole) {
			wasInHole = false;
		}

		if (p.getXRot() < minPitch.getValue()) {
			controlMovement = false;
			cancelJump = false;
			return;
		}

		boolean foundHole = false;
		double targetX = 0;
		double targetZ = 0;

		for (int i = 0; i < maxHeight.getValue(); i++) {
			y--;
			if (y <= mc.level.getMinY() || !isAir(x, y, z)) {
				break;
			}
			if (isHole(x, y, z)) {
				foundHole = true;
				targetX = x + 0.5;
				targetZ = z + 0.5;
				break;
			}
		}

		controlMovement = foundHole;
		cancelJump = foundHole && cancelMove.getValue() && p.getXRot() >= minPitch.getValue();

		if (foundHole) {
			deltaX = Mth.clamp(targetX - p.getX(), -0.05, 0.05);
			deltaZ = Mth.clamp(targetZ - p.getZ(), -0.05, 0.05);
			p.setDeltaMovement(deltaX, p.getDeltaMovement().y - (pull.getValue() ? pullSpeed.getValue() : 0), deltaZ);
		}
	}

	private boolean isHole(int x, int y, int z) {
		return isHoleBlock(x, y - 1, z)
			&& isHoleBlock(x + 1, y, z)
			&& isHoleBlock(x - 1, y, z)
			&& isHoleBlock(x, y, z + 1)
			&& isHoleBlock(x, y, z - 1);
	}

	private boolean isHoleBlock(int x, int y, int z) {
		blockPos.set(x, y, z);
		Block block = mc.level.getBlockState(blockPos).getBlock();
		return block == Blocks.BEDROCK || block == Blocks.OBSIDIAN || block == Blocks.CRYING_OBSIDIAN;
	}

	private boolean isAir(int x, int y, int z) {
		blockPos.set(x, y, z);
		BlockState state = mc.level.getBlockState(blockPos);
		return state.getCollisionShape(mc.level, blockPos).isEmpty();
	}
}
