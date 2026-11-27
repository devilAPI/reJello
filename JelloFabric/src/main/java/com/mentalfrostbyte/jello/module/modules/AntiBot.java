package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventJoinServer;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Marks obvious fake players so the combat modules can ignore them. */
public class AntiBot extends Module {
	private static final List<Entity> BOTS = new ArrayList<>();
	private final BooleanValue ticks = addValue(new BooleanValue("TicksExisted", true));
	private final BooleanValue clear = addValue(new BooleanValue("ClearOnWorldChange", true));
	private final BooleanValue noGround = addValue(new BooleanValue("NoGround", false));
	private final BooleanValue player = addValue(new BooleanValue("Player", true));
	private final BooleanValue living = addValue(new BooleanValue("EntityLivingBase", true));
	private final BooleanValue invisible = addValue(new BooleanValue("Invisible", true));
	private final BooleanValue movement = addValue(new BooleanValue("Movement Action", true));
	private final NumberValue protocols = addValue(new NumberValue("Movement Check Protocols", 3, 1, 4, 1));

	public AntiBot() {
		super("AntiBot", 0, Category.COMBAT);
	}

	@Override
	public void onDisable() {
		BOTS.clear();
	}

	@EventTarget
	public void onJoin(EventJoinServer event) {
		if (clear.getValue()) {
			BOTS.clear();
		}
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.level == null || mc.player == null) {
			return;
		}
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof LivingEntity livingEntity) || entity == mc.player || BOTS.contains(entity)) {
				continue;
			}
			if (player.getValue() && !(entity instanceof Player)) {
				continue;
			}
			if (!isBotLike(livingEntity)) {
				continue;
			}
			BOTS.add(entity);
		}
	}

	private boolean isBotLike(LivingEntity entity) {
		if (ticks.getValue() && entity.tickCount < 20 && entity.distanceTo(mc.player) < 10) {
			return true;
		}
		if (noGround.getValue() && !entity.onGround()) {
			return true;
		}
		if (invisible.getValue() && entity.isInvisible()) {
			return true;
		}
		if (movement.getValue() && protocols.getValue() >= 3 && entity.tickCount < 30) {
			return !entity.isSprinting() && !entity.isShiftKeyDown() && !entity.horizontalCollision && !entity.isInWater();
		}
		return false;
	}

	public static boolean isBot(Entity entity) {
		return entity != null && BOTS.contains(entity);
	}
}
