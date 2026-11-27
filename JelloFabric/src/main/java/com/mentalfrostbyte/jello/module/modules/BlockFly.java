package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Scaffold: keeps a block underneath the player by placing from the hotbar.
 * The 1.8 version also sent synthetic position packets; 26.x places server-authoritatively, so only the
 * click is sent here.
 */
public class BlockFly extends Module {
	public static boolean isEnabled;
	private final TimerUtil timer = new TimerUtil();
	private final TimerUtil refillTimer = new TimerUtil();
	private final ModeValue towerMode = addValue(new ModeValue("TowerMode", "None", "None", "Matrix"));
	private final NumberValue delay = addValue(new NumberValue("Delay", 0, 0, 20, 1));
	private final BooleanValue sprint = addValue(new BooleanValue("Sprint", false));
	private final BooleanValue keepY = addValue(new BooleanValue("KeepY", false));
	private final BooleanValue safeWalk = addValue(new BooleanValue("SafeWalk", true));
	private final BooleanValue eagle = addValue(new BooleanValue("Eagle", false));
	private final BooleanValue placeFix = addValue(new BooleanValue("PlaceFix", true));

	public BlockFly() {
		super("BlockFly", InputConstants.KEY_C, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		isEnabled = true;
		timer.reset();
	}

	@Override
	public void onDisable() {
		isEnabled = false;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		if (!timer.hasTimeElapsed((long) (delay.getValue() * 50) + 1, true)) {
			return;
		}
		LocalPlayer player = mc.player;
		if (sprint.getValue()) {
			player.setSprinting(true);
		}
		if ((safeWalk.getValue() || eagle.getValue()) && !com.mentalfrostbyte.jello.util.PlayerUtil.isNotCollidingBelow(0.6)) {
			mc.options.keyShift.setDown(true);
		} else if (safeWalk.getValue() || eagle.getValue()) {
			mc.options.keyShift.setDown(false);
		}
		int slot = findBlockSlot();
		if (slot == -1) {
			// The 1.8 module pulled blocks from the main inventory into the hotbar before placing.
			if (refillTimer.hasTimeElapsed(400, true)) {
				refillHotbar();
			}
			return;
		}
		BlockPos target = BlockPos.containing(player.getX(), player.getY() - 1.0, player.getZ());
		if (mc.options.keyJump.isDown()) {
			target = target.above();
		}
		BlockState targetState = mc.level.getBlockState(target);
		if (!targetState.canBeReplaced()) {
			return;
		}
		for (Direction direction : Direction.values()) {
			BlockPos support = target.relative(direction);
			BlockState supportState = mc.level.getBlockState(support);
			if (supportState.canBeReplaced() || !supportState.getFluidState().isEmpty()) {
				continue;
			}
			Direction face = direction.getOpposite();
			Vec3 hit = Vec3.atCenterOf(support).add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
			player.setYRot(yawTo(player.getEyePosition(), hit));
			player.setXRot(pitchTo(player.getEyePosition(), hit));
			if (player.getInventory().getSelectedSlot() != slot) {
				player.getInventory().setSelectedSlot(slot);
				if (mc.getConnection() != null) {
					mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
				}
			}
			mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, new BlockHitResult(hit, face, support, false));
			player.swing(InteractionHand.MAIN_HAND, net.minecraft.world.item.component.SwingAnimation.DEFAULT, false);
			return;
		}
	}

	private static void refillHotbar() {
		if (mc.player == null || mc.player.containerMenu != mc.player.inventoryMenu) {
			return;
		}
		List<net.minecraft.world.inventory.Slot> slots = mc.player.inventoryMenu.slots;
		for (int i = 9; i < 36 && i < slots.size(); i++) {
			ItemStack stack = slots.get(i).getItem();
			if (stack.getItem() instanceof BlockItem block && isValidBlock(block.getBlock().defaultBlockState())) {
				mc.gameMode.handleContainerInput(0, i, mc.player.getInventory().getSelectedSlot(),
					net.minecraft.world.inventory.ContainerInput.SWAP, mc.player);
				return;
			}
		}
	}

	private static int findBlockSlot() {
		if (mc.player == null) {
			return -1;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (stack.getItem() instanceof BlockItem block && isValidBlock(block.getBlock().defaultBlockState())) {
				return i;
			}
		}
		return -1;
	}

	private static boolean isValidBlock(BlockState state) {
		return !state.isAir() && state.isSolidRender() && state.getFluidState().isEmpty();
	}

	private static float yawTo(Vec3 eye, Vec3 target) {
		return (float) Math.toDegrees(Math.atan2(target.z - eye.z, target.x - eye.x)) - 90f;
	}

	private static float pitchTo(Vec3 eye, Vec3 target) {
		double dx = target.x - eye.x;
		double dz = target.z - eye.z;
		double dy = target.y - eye.y;
		return (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
	}
}
