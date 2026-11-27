package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Meteor Client auto mount: right-clicks the closest rideable entity. */
public class AutoMount extends Module {
	private final BooleanValue checkSaddle = addValue(new BooleanValue("Check Saddle", false));
	private final BooleanValue rotate = addValue(new BooleanValue("Rotate", true));

	public AutoMount() {
		super("Auto Mount", 0, Category.WORLD);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		if (mc.player.isPassenger() || mc.player.isShiftKeyDown()) {
			return;
		}
		if (mc.player.getMainHandItem().getItem() instanceof SpawnEggItem) {
			return;
		}

		for (Entity entity : mc.level.entitiesForRendering()) {
			if (entity == mc.player || !entity.isAlive() || !rideable(entity)) {
				continue;
			}
			if (entity.distanceToSqr(mc.player) > 16.0) {
				continue;
			}
			if (entity instanceof Mob mob) {
				String path = typePath(entity);
				boolean alwaysNeedsSaddle = path.equals("pig") || path.equals("strider")
					|| path.equals("skeleton_horse") || path.equals("zombie_horse");
				if ((alwaysNeedsSaddle || checkSaddle.getValue()) && !mob.isSaddled()) {
					continue;
				}
			}

			if (rotate.getValue()) {
				lookAt(entity);
			}
			mc.gameMode.interact(mc.player, entity,
				new EntityHitResult(entity, entity.getBoundingBox().getCenter()), InteractionHand.MAIN_HAND);
			return;
		}
	}

	private static boolean rideable(Entity entity) {
		String path = typePath(entity);
		return path.equals("horse") || path.equals("donkey") || path.equals("mule")
			|| path.equals("skeleton_horse") || path.equals("zombie_horse")
			|| path.equals("pig") || path.equals("strider")
			|| path.equals("llama") || path.equals("trader_llama") || path.equals("camel")
			|| path.contains("boat") || path.contains("minecart");
	}

	private static String typePath(Entity entity) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();
	}

	private void lookAt(Entity entity) {
		Vec3 eye = mc.player.getEyePosition();
		double dx = entity.getX() - eye.x;
		double dy = entity.getEyeY() - eye.y;
		double dz = entity.getZ() - eye.z;
		mc.player.setYRot((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
		mc.player.setXRot((float) (-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)))));
	}
}
