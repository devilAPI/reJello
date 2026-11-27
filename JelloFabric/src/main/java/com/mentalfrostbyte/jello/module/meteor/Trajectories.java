package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.event.events.EventTick;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Meteor Client Trajectories: predicts thrown/shot projectile paths.
 *
 * <p>Meteor's ProjectileEntitySimulator is replaced by a compact gravity/drag integration using vanilla bow
 * power and the shared projectile constants. Paths are rebuilt each client tick and drawn with the 2D render
 * event, so the per-frame cost is only projection. Entity hits are not simulated (block collision only).</p>
 */
public class Trajectories extends Module {
	private static final double ARROW_GRAVITY = 0.05;
	private static final double THROWABLE_GRAVITY = 0.03;
	private static final double DRAG = 0.99;

	private final BooleanValue otherPlayers = addValue(new BooleanValue("Other Players", true));
	private final BooleanValue firedProjectiles = addValue(new BooleanValue("Fired Projectiles", false));
	private final BooleanValue ignoreWitherSkulls = addValue(new BooleanValue("Ignore Wither Skulls", false));
	private final BooleanValue accurate = addValue(new BooleanValue("Accurate", false));
	private final NumberValue simulationSteps = addValue(new NumberValue("Simulation Steps", 200, 20, 500, 10));
	private final NumberValue ignoreFirstTicks = addValue(new NumberValue("Ignore Rendering First Ticks", 3, 0, 40, 1));
	private final ModeValue shape = addValue(new ModeValue("Shape Mode", "Both", "Both", "Lines", "Sides"));
	private final BooleanValue renderPositionBoxes = addValue(new BooleanValue("Render Position Boxes", false));
	private final NumberValue positionBoxSize = addValue(new NumberValue("Position Box Size", 0.02, 0.01, 0.1, 0.01));

	private final List<List<Vec3>> paths = new ArrayList<>();
	private final List<Vec3> buffer = new ArrayList<>();

	public Trajectories() {
		super("Trajectories", 0, Category.RENDER);
	}

	@Override
	public void onDisable() {
		paths.clear();
		buffer.clear();
	}

	@EventTarget
	public void onTick(EventTick event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}

		paths.clear();
		if (otherPlayers.getValue()) {
			for (Player player : mc.level.players()) {
				calculatePlayer(player);
			}
		} else {
			calculatePlayer(mc.player);
		}

		if (firedProjectiles.getValue()) {
			for (Entity entity : mc.level.entitiesForRendering()) {
				if (!(entity instanceof Projectile projectile)) {
					continue;
				}
				if (ignoreWitherSkulls.getValue() && projectile instanceof WitherSkull) {
					continue;
				}
				if (projectile instanceof ThrownTrident trident && trident.noPhysics) {
					continue;
				}
				double gravity = projectile instanceof ThrowableProjectile ? THROWABLE_GRAVITY
					: projectile instanceof WitherSkull ? 0.0 : ARROW_GRAVITY;
				simulate(projectile.position(), projectile.getDeltaMovement(), gravity, projectile);
			}
		}
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || mc.player == null) {
			return;
		}
		for (List<Vec3> path : paths) {
			drawPath(event, path);
		}
	}

	private void calculatePlayer(Player player) {
		ItemStack stack = player.getMainHandItem();
		if (!supported(stack.getItem())) {
			stack = player.getOffhandItem();
			if (!supported(stack.getItem())) {
				return;
			}
		}

		Item item = stack.getItem();
		double speed = 1.5;
		double gravity = THROWABLE_GRAVITY;

		if (item instanceof BowItem) {
			int charge = player == mc.player ? Math.min(20, player.getTicksUsingItem()) : 20;
			speed = 3.0 * BowItem.getPowerForTime(charge);
			gravity = ARROW_GRAVITY;
		} else if (item instanceof CrossbowItem) {
			speed = 3.15;
			gravity = ARROW_GRAVITY;
		} else if (item instanceof TridentItem) {
			speed = 2.5;
			gravity = ARROW_GRAVITY;
		}

		if (speed <= 0.01) {
			return;
		}

		simulate(player.getEyePosition(), player.getLookAngle().scale(speed), gravity, player);
	}

	private boolean supported(Item item) {
		return item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem
			|| item instanceof FishingRodItem || item instanceof SnowballItem || item instanceof EggItem
			|| item instanceof EnderpearlItem || item instanceof ExperienceBottleItem
			|| item instanceof ThrowablePotionItem || item instanceof WindChargeItem;
	}

	private void simulate(Vec3 start, Vec3 velocity, double gravity, Entity source) {
		buffer.clear();
		Vec3 pos = start;
		Vec3 vel = velocity;
		int steps = (int) simulationSteps.getValue();
		buffer.add(pos);

		for (int i = 0; i < steps; i++) {
			Vec3 next = pos.add(vel);
			BlockHitResult hit = mc.level.clip(new ClipContext(pos, next,
				ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source));

			if (hit.getType() != HitResult.Type.MISS) {
				buffer.add(hit.getLocation());
				break;
			}

			buffer.add(next);
			pos = next;

			if (accurate.getValue()) {
				vel = new Vec3(vel.x * DRAG, vel.y * DRAG - gravity, vel.z * DRAG);
			} else {
				vel = vel.scale(DRAG).add(0, -gravity, 0);
			}
		}

		if (buffer.size() > 1) {
			paths.add(new ArrayList<>(buffer));
		}
	}

	private void drawPath(EventRender2D event, List<Vec3> points) {
		boolean lines = !shape.is("Sides");
		boolean sides = !shape.is("Lines");
		int start = (int) ignoreFirstTicks.getValue();
		float[] previous = null;
		float[] current = null;

		for (int i = 0; i < points.size(); i++) {
			Vec3 point = points.get(i);
			float[] screen = project(event, point);
			if (screen == null) {
				previous = null;
				continue;
			}

			if (i >= start && previous != null && lines) {
				Render2D.line(event.graphics, previous[0], previous[1], screen[0], screen[1], 1.4f, 0xFFFF9600);
			}

			if (i >= start && renderPositionBoxes.getValue()) {
				double distance = mc.player.getEyePosition().distanceTo(point);
				float half = (float) Math.max(1.0, positionBoxSize.getValue() * 350.0 / Math.max(1.0, distance));
				if (sides) {
					Render2D.rect(event.graphics, screen[0] - half, screen[1] - half,
						screen[0] + half, screen[1] + half, 0x23FF9600);
				}
				if (lines) {
					Render2D.rect(event.graphics, screen[0] - half, screen[1] - half,
						screen[0] + half, screen[1] - half + 1f, 0xFFFF9600);
					Render2D.rect(event.graphics, screen[0] - half, screen[1] + half - 1f,
						screen[0] + half, screen[1] + half, 0xFFFF9600);
					Render2D.rect(event.graphics, screen[0] - half, screen[1] - half,
						screen[0] - half + 1f, screen[1] + half, 0xFFFF9600);
					Render2D.rect(event.graphics, screen[0] + half - 1f, screen[1] - half,
						screen[0] + half, screen[1] + half, 0xFFFF9600);
				}
			}

			previous = screen;
			current = screen;
		}

		if (current != null && lines && points.size() > start) {
			Render2D.rect(event.graphics, current[0] - 1.5f, current[1] - 1.5f,
				current[0] + 1.5f, current[1] + 1.5f, 0xFFFF9600);
		}
	}

	private float[] project(EventRender2D event, Vec3 point) {
		Vec3 p = mc.gameRenderer.projectPointToScreen(point);
		if (p.z < -1 || p.z > 1 || Double.isNaN(p.x) || Double.isNaN(p.y)) {
			return null;
		}
		float x = (float) ((p.x * 0.5 + 0.5) * event.graphics.guiWidth());
		float y = (float) ((-p.y * 0.5 + 0.5) * event.graphics.guiHeight());
		return new float[]{x, y};
	}
}
