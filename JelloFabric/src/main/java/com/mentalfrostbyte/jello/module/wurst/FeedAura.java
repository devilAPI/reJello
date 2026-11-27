package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.EntityHitResult;

/** Wurst FeedAura: feeds nearby animals to breed them. */
public class FeedAura extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 5, 1, 10, 0.05));
	private final BooleanValue filterBabies = addValue(new BooleanValue("Filter Babies", true));
	private final BooleanValue filterUntamed = addValue(new BooleanValue("Filter Untamed", false));

	public FeedAura() {
		super("Feed Aura", 0, Category.MISC);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		double rangeSq = range.getValue() * range.getValue();
		ItemStack held = mc.player.getMainHandItem();
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof Animal animal) || !animal.isAlive()) {
				continue;
			}
			if (!animal.isFood(held) || !animal.canFallInLove()) {
				continue;
			}
			if (filterBabies.getValue() && animal.isBaby()) {
				continue;
			}
			if (filterUntamed.getValue() && animal instanceof TamableAnimal tamable && !tamable.isTame()) {
				continue;
			}
			if (mc.player.distanceToSqr(animal) > rangeSq) {
				continue;
			}
			lookAt(animal);
			mc.gameMode.interact(mc.player, animal,
				new EntityHitResult(animal, animal.getBoundingBox().getCenter()), InteractionHand.MAIN_HAND);
			mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
			return;
		}
	}

	private void lookAt(Entity entity) {
		lookAt(entity.getBoundingBox().getCenter());
	}

	private void lookAt(net.minecraft.world.phys.Vec3 target) {
		net.minecraft.world.phys.Vec3 eye = mc.player.getEyePosition();
		double dx = target.x - eye.x;
		double dy = target.y - eye.y;
		double dz = target.z - eye.z;
		mc.player.setYRot((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
		mc.player.setXRot((float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))));
	}
}
