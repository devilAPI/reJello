package com.mentalfrostbyte.jello.mixin;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.event.events.EventMove;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Replaces 1.8's {@code Entity.moveEntity} hook: modules can rewrite the local player's movement vector. */
@Mixin(Entity.class)
public class EntityMixin {
	@ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
	private Vec3 jello$move(Vec3 delta) {
		if ((Object) this != Minecraft.getInstance().player) {
			return delta;
		}
		EventMove e = new EventMove(delta.x, delta.y, delta.z);
		EventManager.call(e);
		if (e.x != delta.x || e.y != delta.y || e.z != delta.z) {
			return new Vec3(e.x, e.y, e.z);
		}
		return delta;
	}
}
