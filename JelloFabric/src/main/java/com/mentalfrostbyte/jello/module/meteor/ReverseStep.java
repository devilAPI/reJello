package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BedBlock;

/** Meteor Client reverse-step: snaps you down to the ground faster. */
public class ReverseStep extends Module {
	private final NumberValue fallSpeed = addValue(new NumberValue("Fall Speed", 3.0, 0.0, 10.0, 0.1));
	private final NumberValue fallDistance = addValue(new NumberValue("Fall Distance", 3.0, 0.0, 10.0, 0.1));
	private final BooleanValue vehicles = addValue(new BooleanValue("Vehicles", false));

	public ReverseStep() {
		super("Reverse Step", 0, Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.level == null) {
			return;
		}
		Entity vehicle = p.getVehicle();
		if (vehicle != null && vehicles.getValue()) {
			if (canSnap(vehicle)) {
				vehicle.setDeltaMovement(vehicle.getDeltaMovement().x, -fallSpeed.getValue(), vehicle.getDeltaMovement().z);
			}
			return;
		}

		if (p.isSuppressingSlidingDownLadder() || (p.zza == 0 && p.xxa == 0)) {
			return;
		}
		if (!isOnBed() && canSnap(p)) {
			p.setDeltaMovement(p.getDeltaMovement().x, -fallSpeed.getValue(), p.getDeltaMovement().z);
		}
	}

	private boolean canSnap(Entity entity) {
		if (!entity.onGround() || entity.isUnderWater() || entity.isInLava()
			|| mc.options.keyJump.isDown() || entity.noPhysics) {
			return false;
		}
		return !mc.level.noCollision(entity, entity.getBoundingBox().move(0.0, -(fallDistance.getValue() + 0.01), 0.0));
	}

	private boolean isOnBed() {
		LocalPlayer p = mc.player;
		BlockPos.MutableBlockPos pos = p.blockPosition().mutable();

		if (check(pos, 0, 0)) {
			return true;
		}
		double xa = p.getX() - pos.getX();
		double za = p.getZ() - pos.getZ();

		if (xa >= 0 && xa <= 0.3 && check(pos, -1, 0)) {
			return true;
		}
		if (xa >= 0.7 && check(pos, 1, 0)) {
			return true;
		}
		if (za >= 0 && za <= 0.3 && check(pos, 0, -1)) {
			return true;
		}
		if (za >= 0.7 && check(pos, 0, 1)) {
			return true;
		}
		if (xa >= 0 && xa <= 0.3 && za >= 0 && za <= 0.3 && check(pos, -1, -1)) {
			return true;
		}
		if (xa >= 0 && xa <= 0.3 && za >= 0.7 && check(pos, -1, 1)) {
			return true;
		}
		if (xa >= 0.7 && za >= 0 && za <= 0.3 && check(pos, 1, -1)) {
			return true;
		}
		return xa >= 0.7 && za >= 0.7 && check(pos, 1, 1);
	}

	private boolean check(BlockPos.MutableBlockPos pos, int x, int z) {
		pos.move(x, 0, z);
		boolean bed = mc.level.getBlockState(pos).getBlock() instanceof BedBlock;
		pos.move(-x, 0, -z);
		return bed;
	}
}
