package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.phys.Vec3;

/** Meteor Client enderman look: looks at or away from endermen, optionally stunning hostile ones. */
public class EndermanLook extends Module {
	private final ModeValue lookMode = addValue(new ModeValue("Look Mode", "Away", "Away", "At"));
	private final BooleanValue stunHostiles = addValue(new BooleanValue("Stun Hostiles", true));

	public EndermanLook() {
		super("Enderman Look", 0, Category.WORLD);
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (mc.player.getItemBySlot(EquipmentSlot.HEAD).typeHolder()
			.is(ItemTags.GAZE_DISGUISE_EQUIPMENT)
			|| mc.player.getAbilities().instabuild) {
			return;
		}

		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof Enderman enderman) || !enderman.isAlive()
				|| !mc.player.hasLineOfSight(enderman)) {
				continue;
			}

			if (lookMode.is("Away")) {
				if (enderman.isCreepy() && stunHostiles.getValue()) {
					lookAt(enderman);
				} else if (isBeingStaredBy(enderman)) {
					mc.player.setXRot(90.0f);
				}
			} else if (!enderman.isCreepy()) {
				lookAt(enderman);
			}
		}
	}

	/** Copy of {@code Enderman#isBeingStaredBy}, which is private in 26.x. */
	private boolean isBeingStaredBy(Enderman enderman) {
		Vec3 view = mc.player.getViewVector(1.0f).normalize();
		Vec3 toEnderman = new Vec3(
			enderman.getX() - mc.player.getX(),
			enderman.getEyeY() - mc.player.getEyeY(),
			enderman.getZ() - mc.player.getZ());
		double length = toEnderman.length();
		toEnderman = toEnderman.normalize();
		double dot = view.dot(toEnderman);
		return dot > 1.0 - 0.025 / length;
	}

	private void lookAt(Enderman enderman) {
		double dx = enderman.getX() - mc.player.getX();
		double dy = enderman.getEyeY() - mc.player.getEyeY();
		double dz = enderman.getZ() - mc.player.getZ();
		mc.player.setYRot((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
		mc.player.setXRot((float) (-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)))));
	}
}
