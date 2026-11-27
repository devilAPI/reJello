package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Meteor Client jesus: walk on water and lava.
 * The collision/fluid hooks (CanWalkOnFluid, fluid shape) are mixins upstream; this port keeps the
 * vertical movement behaviour and exposes {@link #canWalkOnPowderSnow()} for those hooks.
 */
public class Jesus extends Module {
	private final BooleanValue powderSnow = addValue(new BooleanValue("Powder Snow", true));
	private final BooleanValue ncpBypass = addValue(new BooleanValue("NCP Bypass", false));
	private final BooleanValue slowDown = addValue(new BooleanValue("Slow Down", false));

	private final ModeValue waterMode = addValue(new ModeValue("Water Mode", "Solid", "Solid", "Ignore"));
	private final BooleanValue dipIfBurning = addValue(new BooleanValue("Dip If Burning", true));
	private final BooleanValue dipOnSneakWater = addValue(new BooleanValue("Dip On Sneak (Water)", true));
	private final BooleanValue dipOnFallWater = addValue(new BooleanValue("Dip On Fall (Water)", true));
	private final NumberValue dipFallHeightWater = addValue(new NumberValue("Dip Fall Height (Water)", 4, 1, 20, 1));

	private final ModeValue lavaMode = addValue(new ModeValue("Lava Mode", "Solid", "Solid", "Ignore"));
	private final BooleanValue dipIfFireResistant = addValue(new BooleanValue("Dip If Fire Resistant", true));
	private final BooleanValue dipOnSneakLava = addValue(new BooleanValue("Dip On Sneak (Lava)", true));
	private final BooleanValue dipOnFallLava = addValue(new BooleanValue("Dip On Fall (Lava)", true));
	private final NumberValue dipFallHeightLava = addValue(new NumberValue("Dip Fall Height (Lava)", 4, 1, 20, 1));

	private int ascending = 10;

	public Jesus() {
		super("Jesus", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		ascending = 10;
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.level == null) {
			return;
		}
		if (p.isVisuallySwimming()) {
			return;
		}
		if (p.isInWater() && !waterShouldBeSolid(p)) {
			return;
		}
		if (p.isInLava() && !lavaShouldBeSolid(p)) {
			return;
		}

		Entity moving = p.isPassenger() && p.getVehicle() != null ? p.getVehicle() : p;

		boolean bubbleColumn = mc.level.getBlockState(p.blockPosition()).getBlock() == Blocks.BUBBLE_COLUMN;
		if (bubbleColumn) {
			if (mc.options.keyJump.isDown() && moving.getDeltaMovement().y < 0.11) {
				moving.setDeltaMovement(moving.getDeltaMovement().x, 0.11, moving.getDeltaMovement().z);
			}
			return;
		}

		if (moving.isInWater() || moving.isInLava()) {
			moving.setDeltaMovement(moving.getDeltaMovement().x, 0.11, moving.getDeltaMovement().z);
			ascending = 0;
			return;
		}

		BlockState below = mc.level.getBlockState(moving.blockPosition().below());
		boolean waterLogged = below.getValueOrElse(BlockStateProperties.WATERLOGGED, false);

		if (ascending == 0) {
			moving.setDeltaMovement(moving.getDeltaMovement().x, 0.11, moving.getDeltaMovement().z);
		} else if (ascending == 1
			&& (below.getBlock() == Blocks.WATER || below.getBlock() == Blocks.LAVA || waterLogged)) {
			moving.setDeltaMovement(moving.getDeltaMovement().x, 0, moving.getDeltaMovement().z);
		}
		ascending++;
	}

	private boolean waterShouldBeSolid(LocalPlayer p) {
		if (gameMode(p) == GameType.SPECTATOR || p.getAbilities().flying) {
			return false;
		}
		if (p.getVehicle() instanceof AbstractBoat) {
			return false;
		}
		if (dipIfBurning.getValue() && p.isOnFire()) {
			return false;
		}
		if (dipOnSneakWater.getValue() && mc.options.keyShift.isDown()) {
			return false;
		}
		if (dipOnFallWater.getValue() && p.fallDistance > dipFallHeightWater.getValue()) {
			return false;
		}
		return waterMode.is("Solid");
	}

	private boolean lavaShouldBeSolid(LocalPlayer p) {
		if (gameMode(p) == GameType.SPECTATOR || p.getAbilities().flying) {
			return false;
		}
		if (p.getVehicle() instanceof Strider) {
			return false;
		}
		if (isLavaDangerous(p) && lavaMode.is("Solid")) {
			return true;
		}
		if (dipOnSneakLava.getValue() && mc.options.keyShift.isDown()) {
			return false;
		}
		if (dipOnFallLava.getValue() && p.fallDistance > dipFallHeightLava.getValue()) {
			return false;
		}
		return lavaMode.is("Solid");
	}

	private static boolean isLavaDangerous(LocalPlayer p) {
		return !p.hasEffect(MobEffects.FIRE_RESISTANCE);
	}

	private static GameType gameMode(LocalPlayer p) {
		if (p.isCreative()) {
			return GameType.CREATIVE;
		}
		if (p.isSpectator()) {
			return GameType.SPECTATOR;
		}
		return GameType.SURVIVAL;
	}

	public boolean canWalkOnPowderSnow() {
		return isToggled() && powderSnow.getValue();
	}

	public boolean ncpBypass() {
		return isToggled() && ncpBypass.getValue();
	}

	public boolean slowDown() {
		return isToggled() && slowDown.getValue();
	}
}
