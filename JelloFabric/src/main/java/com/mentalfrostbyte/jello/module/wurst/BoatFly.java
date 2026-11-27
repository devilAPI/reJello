package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/** Wurst BoatFly: lets boats and other vehicles fly. */
public class BoatFly extends Module {
	private final BooleanValue changeForwardSpeed = addValue(new BooleanValue("Change Forward Speed", false));
	private final NumberValue forwardSpeed = addValue(new NumberValue("Forward Speed", 1, 0.05, 5, 0.05));
	private final NumberValue upwardSpeed = addValue(new NumberValue("Upward Speed", 0.3, 0, 5, 0.05));

	public BoatFly() {
		super("Boat Fly", 0, Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || !mc.player.isPassenger()) {
			return;
		}
		Entity vehicle = mc.player.getVehicle();
		if (vehicle == null) {
			return;
		}
		Vec3 velocity = vehicle.getDeltaMovement();
		double motionX = velocity.x;
		double motionY = 0;
		double motionZ = velocity.z;

		if (mc.options.keyJump.isDown()) {
			motionY = upwardSpeed.getValue();
		} else if (mc.options.keySprint.isDown()) {
			motionY = velocity.y;
		}

		if (mc.options.keyUp.isDown() && changeForwardSpeed.getValue()) {
			double speed = forwardSpeed.getValue();
			float yawRad = vehicle.getYRot() * Mth.DEG_TO_RAD;
			motionX = Mth.sin(-yawRad) * speed;
			motionZ = Mth.cos(yawRad) * speed;
		}

		vehicle.setDeltaMovement(motionX, motionY, motionZ);
	}
}
