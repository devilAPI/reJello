package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventMove;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.player.LocalPlayer;

/** Meteor Client elytra-fly: Vanilla / Packet / Pitch40 / Bounce flight modes. */
public class ElytraFly extends Module {
	private final ModeValue flightMode = addValue(new ModeValue("Mode", "Vanilla", "Vanilla", "Packet", "Pitch40", "Bounce"));
	private final BooleanValue autoTakeOff = addValue(new BooleanValue("Auto Take Off", false));
	private final NumberValue fallMultiplier = addValue(new NumberValue("Fall Multiplier", 0.01, 0.0, 10.0, 0.01));
	private final NumberValue horizontalSpeed = addValue(new NumberValue("Horizontal Speed", 1.0, 0.0, 10.0, 0.1));
	private final NumberValue verticalSpeed = addValue(new NumberValue("Vertical Speed", 1.0, 0.0, 10.0, 0.1));
	private final BooleanValue acceleration = addValue(new BooleanValue("Acceleration", false));
	private final NumberValue accelerationStep = addValue(new NumberValue("Acceleration Step", 1.0, 0.1, 5.0, 0.1));
	private final NumberValue accelerationStart = addValue(new NumberValue("Acceleration Start", 0.1, 0.1, 10.0, 0.1));
	private final BooleanValue stopInWater = addValue(new BooleanValue("Stop In Water", true));
	private final BooleanValue noUnloadedChunks = addValue(new BooleanValue("No Unloaded Chunks", true));
	private final BooleanValue autoHover = addValue(new BooleanValue("Auto Hover", false));
	private final BooleanValue noCrash = addValue(new BooleanValue("No Crash", false));
	private final NumberValue crashLookAhead = addValue(new NumberValue("Crash Look Ahead", 5, 1, 15, 1));
	private final BooleanValue instaDrop = addValue(new BooleanValue("Insta Drop", false));

	private final NumberValue pitch40LowerBounds = addValue(new NumberValue("Pitch40 Lower Bounds", 180, -128, 320, 1));
	private final NumberValue pitch40UpperBounds = addValue(new NumberValue("Pitch40 Upper Bounds", 220, -128, 320, 1));
	private final NumberValue pitch40RotateUp = addValue(new NumberValue("Pitch40 Rotate Speed Up", 5.45, 1.0, 20.0, 0.05));
	private final NumberValue pitch40RotateDown = addValue(new NumberValue("Pitch40 Rotate Speed Down", 0.9, 0.5, 2.0, 0.05));

	private final BooleanValue autoJump = addValue(new BooleanValue("Auto Jump", true));
	private final BooleanValue lockPitch = addValue(new BooleanValue("Pitch Lock", true));
	private final NumberValue pitch = addValue(new NumberValue("Pitch", 85, 0, 90, 1));
	private final BooleanValue restart = addValue(new BooleanValue("Restart", true));
	private final NumberValue restartDelay = addValue(new NumberValue("Restart Delay", 7, 0, 20, 1));
	private final BooleanValue sprintConstantly = addValue(new BooleanValue("Sprint Constantly", true));
	private final BooleanValue manualTakeoff = addValue(new BooleanValue("Manual Takeoff", false));

	private final BooleanValue elytraReplace = addValue(new BooleanValue("Elytra Replace", false));
	private final NumberValue replaceDurability = addValue(new NumberValue("Replace Durability", 2, 1, 500, 1));
	private final ModeValue chestSwap = addValue(new ModeValue("Chest Swap", "Never", "Always", "Never", "WaitForGround"));

	private final BooleanValue autoPilot = addValue(new BooleanValue("Auto Pilot", false));
	private final BooleanValue useFireworks = addValue(new BooleanValue("Use Fireworks", false));
	private final NumberValue fireworkDelay = addValue(new NumberValue("Firework Delay", 8, 1, 20, 1));
	private final NumberValue minimumHeight = addValue(new NumberValue("Minimum Height", 120, -128, 320, 1));

	private double velX, velY, velZ, accelerationValue, fireworkTicks;
	private int jumpTimer, restartTimer;
	private boolean lastJumpPressed, lastForwardPressed, incrementJumpTimer, rubberbanded, wasFallFlying;
	private double lastX, lastY, lastZ;
	private boolean pitch40Down = true;
	private float pitch40Pitch = 37.72f;

	public ElytraFly() {
		super("Elytra Fly", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		jumpTimer = 0;
		restartTimer = (int) restartDelay.getValue();
		fireworkTicks = 0;
		accelerationValue = 0;
		rubberbanded = false;
		pitch40Down = true;
		pitch40Pitch = 37.72f;
		LocalPlayer p = mc.player;
		if (p != null) {
			lastX = p.getX();
			lastY = p.getY();
			lastZ = p.getZ();
		}
		if (p != null && !chestSwap.is("Never") && !isWearingElytra(p)) {
			swapChest(p);
		}
	}

	@Override
	public void onDisable() {
		mc.options.keyUp.setDown(false);
		LocalPlayer p = mc.player;
		if (p != null) {
			if (chestSwap.is("Always") && isWearingElytra(p)) {
				swapChest(p);
			}
			if (flightMode.is("Packet")) {
				p.getAbilities().flying = false;
			}
		}
		lastForwardPressed = false;
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.level == null) {
			return;
		}

		// Rubberband heuristic: a teleport of more than a few blocks between ticks.
		double dx = p.getX() - lastX;
		double dy = p.getY() - lastY;
		double dz = p.getZ() - lastZ;
		if (dx * dx + dy * dy + dz * dz > 64 && wasFallFlying) {
			rubberbanded = true;
			restartTimer = (int) restartDelay.getValue();
		}
		lastX = p.getX();
		lastY = p.getY();
		lastZ = p.getZ();
		wasFallFlying = p.isFallFlying();

		if (instaDrop.getValue() && p.isFallFlying()) {
			p.setDeltaMovement(Vec3.ZERO);
		}

		if (flightMode.is("Packet")) {
			tickPacket(p);
		} else if (flightMode.is("Bounce")) {
			tickBounce(p);
		} else if (flightMode.is("Pitch40") && p.isFallFlying()) {
			tickPitch40(p);
		}
	}

	@EventTarget
	public void onMove(EventMove event) {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.level == null || !isWearingElytra(p)) {
			return;
		}
		if (flightMode.is("Bounce")) {
			return;
		}

		autoTakeoff(p);

		if (!p.isFallFlying()) {
			if (lastForwardPressed) {
				mc.options.keyUp.setDown(false);
				lastForwardPressed = false;
			}
			return;
		}

		velX = 0;
		velY = event.y;
		velZ = 0;
		Vec3 forward = Vec3.directionFromRotation(0, p.getYRot()).scale(0.1);
		Vec3 right = Vec3.directionFromRotation(0, p.getYRot() + 90).scale(0.1);

		if (p.isInWater() && stopInWater.getValue()) {
			sendStartFallFlying(p);
			return;
		}

		// Fall multiplier: never let vanilla gravity push us up.
		if (velY < 0) {
			velY *= fallMultiplier.getValue();
		} else if (velY > 0) {
			velY = 0;
		}

		handleAutopilot(p);
		handleAcceleration(p);
		handleHorizontal(p, forward, right);
		handleVertical(p);

		int chunkX = Mth.floor((p.getX() + velX) / 16.0);
		int chunkZ = Mth.floor((p.getZ() + velZ) / 16.0);
		if (noUnloadedChunks.getValue() && !mc.level.getChunkSource().hasChunk(chunkX, chunkZ)) {
			accelerationValue = 0;
			event.x = 0;
			event.y = velY;
			event.z = 0;
		} else {
			event.x = velX;
			event.y = velY;
			event.z = velZ;
		}

		if (flightMode.is("Packet")) {
			p.getAbilities().flying = true;
			p.getAbilities().setFlyingSpeed((float) (horizontalSpeed.getValue() / 20.0));
		}

		if (noCrash.getValue() && p.isFallFlying()) {
			Vec3 delta = p.getDeltaMovement();
			if (delta.lengthSqr() > 1.0E-6) {
				Vec3 lookAhead = p.position().add(delta.normalize().scale(crashLookAhead.getValue()));
				ClipContext context = new ClipContext(p.position(), new Vec3(lookAhead.x, p.getY(), lookAhead.z),
					ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p);
				BlockHitResult hit = mc.level.clip(context);
				if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
					event.x = 0;
					event.z = 0;
				}
			}
		}

		if (autoHover.getValue() && p.input.keyPresses.shift() && p.isFallFlying()) {
			BlockPos belowPos = p.blockPosition().below();
			BlockState below = mc.level.getBlockState(belowPos);
			BlockState below2 = mc.level.getBlockState(belowPos.below());
			boolean belowCollidable = !below.getCollisionShape(mc.level, belowPos).isEmpty() || !below.getFluidState().isEmpty();
			boolean below2Collidable = !below2.getCollisionShape(mc.level, belowPos.below()).isEmpty() || !below2.getFluidState().isEmpty();

			if (!belowCollidable && below2Collidable) {
				event.y = -0.1;
				p.setXRot(Mth.clamp(p.getXRot(), -50f, 20f));
			}
			if (belowCollidable) {
				event.y = -0.03;
				p.setXRot(Mth.clamp(p.getXRot(), -50f, 20f));
				if (p.getY() <= belowPos.getY() + 1.34f) {
					event.y = 0;
					mc.options.keyShift.setDown(false);
				}
			}
		}
	}

	private void autoTakeoff(LocalPlayer p) {
		if (incrementJumpTimer) {
			jumpTimer++;
		}
		boolean jumpPressed = mc.options.keyJump.isDown();
		if (autoTakeOff.getValue() && jumpPressed && !flightMode.is("Pitch40") && !flightMode.is("Bounce")) {
			if (!lastJumpPressed && !p.isFallFlying()) {
				jumpTimer = 0;
				incrementJumpTimer = true;
			}
			if (jumpTimer >= 8) {
				jumpTimer = 0;
				incrementJumpTimer = false;
				p.setJumping(false);
				p.setSprinting(true);
				p.jumpFromGround();
				sendStartFallFlying(p);
			}
		}
		lastJumpPressed = jumpPressed;
	}

	private void handleHorizontal(LocalPlayer p, Vec3 forward, Vec3 right) {
		boolean moved = false;
		boolean strafed = false;
		double speed = acceleration.getValue() ? accelerationValue : horizontalSpeed.getValue();

		if (mc.options.keyUp.isDown()) {
			velX += forward.x * speed * 10;
			velZ += forward.z * speed * 10;
			moved = true;
		} else if (mc.options.keyDown.isDown()) {
			velX -= forward.x * speed * 10;
			velZ -= forward.z * speed * 10;
			moved = true;
		}
		if (mc.options.keyRight.isDown()) {
			velX += right.x * speed * 10;
			velZ += right.z * speed * 10;
			strafed = true;
		} else if (mc.options.keyLeft.isDown()) {
			velX -= right.x * speed * 10;
			velZ -= right.z * speed * 10;
			strafed = true;
		}
		if (moved && strafed) {
			double diagonal = 1 / Math.sqrt(2);
			velX *= diagonal;
			velZ *= diagonal;
		}
	}

	private void handleVertical(LocalPlayer p) {
		if (mc.options.keyJump.isDown()) {
			velY += 0.5 * verticalSpeed.getValue();
		} else if (mc.options.keyShift.isDown()) {
			velY -= 0.5 * verticalSpeed.getValue();
		}
	}

	private void handleAcceleration(LocalPlayer p) {
		if (acceleration.getValue()) {
			if (!p.input.keyPresses.forward() && !p.input.keyPresses.backward()
				&& !p.input.keyPresses.left() && !p.input.keyPresses.right()) {
				accelerationValue = 0;
			}
			accelerationValue = Math.min(accelerationValue + accelerationStart.getValue() + accelerationStep.getValue() * 0.1,
				horizontalSpeed.getValue());
		} else {
			accelerationValue = 0;
		}
	}

	private void handleAutopilot(LocalPlayer p) {
		if (autoPilot.getValue() && p.getY() > minimumHeight.getValue()) {
			mc.options.keyUp.setDown(true);
			lastForwardPressed = true;
		}
		if (useFireworks.getValue()) {
			if (fireworkTicks <= 0) {
				fireworkTicks = fireworkDelay.getValue() * 20;
				useFirework(p);
			}
			fireworkTicks--;
		}
	}

	private void useFirework(LocalPlayer p) {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = p.getInventory().getItem(i);
			if (stack.getItem() == Items.FIREWORK_ROCKET) {
				if (p.getInventory().getSelectedSlot() != i) {
					p.getInventory().setSelectedSlot(i);
					if (mc.getConnection() != null) {
						mc.getConnection().send(new ServerboundSetCarriedItemPacket(i));
					}
				}
				if (mc.gameMode != null) {
					mc.gameMode.useItem(p, InteractionHand.MAIN_HAND);
				}
				return;
			}
		}
	}

	private void tickPacket(LocalPlayer p) {
		if (!isWearingElytra(p) || p.fallDistance <= 0.2 || mc.options.keyShift.isDown()) {
			return;
		}
		Vec3 velocity = Vec3.ZERO;
		if (mc.options.keyUp.isDown()) {
			velocity = velocity.add(0, 0, horizontalSpeed.getValue())
				.yRot(-(float) Math.toRadians(p.getYRot()));
		} else if (mc.options.keyDown.isDown()) {
			velocity = velocity.add(0, 0, horizontalSpeed.getValue())
				.yRot((float) Math.toRadians(p.getYRot()));
		}
		velocity = velocity.add(0, mc.options.keyJump.isDown() ? verticalSpeed.getValue() : -verticalSpeed.getValue(), 0);
		p.setDeltaMovement(velocity);
		sendStartFallFlying(p);
	}

	private void tickPitch40(LocalPlayer p) {
		if (pitch40Down && p.getY() <= pitch40LowerBounds.getValue()) {
			pitch40Down = false;
		} else if (!pitch40Down && p.getY() >= pitch40UpperBounds.getValue()) {
			pitch40Down = true;
		}
		if (!pitch40Down) {
			pitch40Pitch -= (float) (pitch40RotateUp.getValue() + (1.0 * (Math.random() - 0.5)));
			if (pitch40Pitch < -54.77f) {
				pitch40Pitch = -54.77f;
				pitch40Down = true;
			}
		} else if (pitch40Pitch < 37.72f) {
			pitch40Pitch += (float) (pitch40RotateDown.getValue() + (0.5 * (Math.random() - 0.5)));
		}
		p.setXRot(pitch40Pitch);
	}

	private void tickBounce(LocalPlayer p) {
		if (mc.options.keyJump.isDown() && !p.isFallFlying() && !manualTakeoff.getValue()) {
			sendStartFallFlying(p);
		}
		if (bounceConditions(p)) {
			if (!rubberbanded) {
				if (autoJump.getValue()) {
					mc.options.keyJump.setDown(true);
				}
				mc.options.keyUp.setDown(true);
				p.setYRot(Math.round((p.getYRot() + 1f) / 45f) * 45f);
				if (lockPitch.getValue()) {
					p.setXRot((float) pitch.getValue());
				}
			}
			if (sprintConstantly.getValue()) {
				p.setSprinting(true);
			}
			if (rubberbanded && restart.getValue()) {
				if (restartTimer > 0) {
					restartTimer--;
				} else {
					sendStartFallFlying(p);
					rubberbanded = false;
					restartTimer = (int) restartDelay.getValue();
				}
			}
		}
	}

	private static boolean bounceConditions(LocalPlayer p) {
		return !p.getAbilities().flying && !p.isPassenger() && !p.onClimbable()
			&& !p.isInWater() && !p.hasEffect(net.minecraft.world.effect.MobEffects.LEVITATION);
	}

	private static boolean isWearingElytra(LocalPlayer p) {
		return p.getItemBySlot(EquipmentSlot.CHEST).has(DataComponents.GLIDER);
	}

	private static void sendStartFallFlying(LocalPlayer p) {
		if (p.connection != null) {
			p.connection.send(new ServerboundPlayerCommandPacket(p, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
		}
	}

	private void swapChest(LocalPlayer p) {
		if (mc.gameMode == null || p.inventoryMenu == null) {
			return;
		}
		var slots = p.inventoryMenu.slots;
		int best = -1;
		int bestDurability = -1;
		for (int i = 0; i < slots.size(); i++) {
			ItemStack stack = slots.get(i).getItem();
			if (stack.getItem() == Items.ELYTRA) {
				int left = stack.getMaxDamage() - stack.getDamageValue();
				if (left > replaceDurability.getValue() && left > bestDurability) {
					best = i;
					bestDurability = left;
				}
			}
		}
		if (best != -1) {
			mc.gameMode.handleContainerInput(p.inventoryMenu.containerId, best, 0, ContainerInput.QUICK_MOVE, p);
		}
	}
}
