package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.Random;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/** Meteor Client auto-fish: casts, watches the bobber and reels in. */
public class AutoFish extends Module {
	public AutoFish() {
		super("Auto Fish", 0, Category.PLAYER);
	}

	private final BooleanValue autoSwitch = addValue(new BooleanValue("Auto Switch", true));
	private final BooleanValue antiBreak = addValue(new BooleanValue("Anti Break", true));
	private final BooleanValue autoCast = addValue(new BooleanValue("Auto Cast", true));
	private final NumberValue castDelay = addValue(new NumberValue("Cast Delay", 14, 1, 100, 1));
	private final NumberValue castDelayVariance = addValue(new NumberValue("Cast Variance", 0, 0, 30, 1));
	private final NumberValue catchDelay = addValue(new NumberValue("Catch Delay", 6, 1, 100, 1));
	private final NumberValue catchDelayVariance = addValue(new NumberValue("Catch Variance", 0, 0, 10, 1));

	private final Random random = new Random();
	private int state;
	private double castDelayLeft;
	private double catchDelayLeft;

	@Override
	public void onEnable() {
		state = 0;
		castDelayLeft = 0;
		catchDelayLeft = 0;
	}

	@Override
	public void onDisable() {
		state = 0;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}
		if (mc.player.fishing != null) {
			handleFishing(mc.player.fishing);
		} else {
			handleIdle();
		}
	}

	private void handleIdle() {
		state = 0;
		RodCandidate candidate = findBestRod();
		if (candidate == null || !prepareRod(candidate)) {
			return;
		}
		if (!autoCast.getValue()) {
			return;
		}
		if (castDelayLeft > 0) {
			castDelayLeft -= 1;
			return;
		}
		cast();
	}

	private void handleFishing(FishingHook hook) {
		switch (state) {
			case 0 -> state = 1;
			case 1 -> {
				if (hook.getHookedIn() != null) {
					reel();
					return;
				}
				// The bite is synced by the hook dipping sharply into the water.
				if (hook.isInWater() && hook.getDeltaMovement().y < -0.2) {
					catchDelayLeft = randomize(catchDelay.getValue(), catchDelayVariance.getValue());
					state = 2;
				}
			}
			case 2 -> {
				if (hook.getHookedIn() != null) {
					reel();
					return;
				}
				if (catchDelayLeft > 0) {
					catchDelayLeft -= 1;
					return;
				}
				reel();
			}
			default -> state = 0;
		}
	}

	private void cast() {
		if (useRod()) {
			state = 1;
		}
	}

	private void reel() {
		if (useRod()) {
			state = 0;
		}
	}

	private boolean useRod() {
		RodCandidate candidate = findBestRod();
		if (candidate == null || !prepareRod(candidate)) {
			return false;
		}
		mc.gameMode.useItem(mc.player, candidate.hand());
		castDelayLeft = randomize(castDelay.getValue(), castDelayVariance.getValue());
		return true;
	}

	private boolean prepareRod(RodCandidate candidate) {
		if (candidate.hand() == InteractionHand.OFF_HAND) {
			return true;
		}
		if (candidate.hotbarSlot() == mc.player.getInventory().getSelectedSlot()) {
			return true;
		}
		if (!autoSwitch.getValue()) {
			return false;
		}
		mc.player.getInventory().setSelectedSlot(candidate.hotbarSlot());
		return candidate.hotbarSlot() == mc.player.getInventory().getSelectedSlot();
	}

	private boolean usableRod(ItemStack stack) {
		if (!(stack.getItem() instanceof FishingRodItem)) {
			return false;
		}
		return !antiBreak.getValue() || stack.getDamageValue() != stack.getMaxDamage() - 1;
	}

	private static int scoreRod(ItemStack stack) {
		int score = 0;
		score += enchantLevel(stack, Enchantments.LUCK_OF_THE_SEA);
		score += enchantLevel(stack, Enchantments.LURE);
		score += enchantLevel(stack, Enchantments.MENDING);
		score += enchantLevel(stack, Enchantments.UNBREAKING);
		return score;
	}

	private static int enchantLevel(ItemStack stack, ResourceKey<Enchantment> enchantment) {
		ItemEnchantments enchantments = stack.getEnchantments();
		for (Holder<Enchantment> holder : enchantments.keySet()) {
			if (holder.is(enchantment)) {
				return enchantments.getLevel(holder);
			}
		}
		return 0;
	}

	private RodCandidate findBestRod() {
		int selected = mc.player.getInventory().getSelectedSlot();
		RodCandidate best = null;

		ItemStack main = mc.player.getMainHandItem();
		if (usableRod(main)) {
			best = new RodCandidate(InteractionHand.MAIN_HAND, selected, scoreRod(main));
		}
		for (int i = 0; i < 9; i++) {
			if (i == selected) {
				continue;
			}
			ItemStack stack = mc.player.getInventory().getItem(i);
			if (!usableRod(stack)) {
				continue;
			}
			int score = scoreRod(stack);
			if (best == null || score > best.score()) {
				best = new RodCandidate(InteractionHand.MAIN_HAND, i, score);
			}
		}
		ItemStack offhand = mc.player.getOffhandItem();
		if (usableRod(offhand)) {
			int score = scoreRod(offhand);
			if (best == null || score > best.score()) {
				best = new RodCandidate(InteractionHand.OFF_HAND, -1, score);
			}
		}
		return best;
	}

	private double randomize(double delay, double variance) {
		if (variance <= 0) {
			return Math.max(1, delay);
		}
		return Math.max(1, delay + random.nextInt((int) variance + 1));
	}

	private record RodCandidate(InteractionHand hand, int hotbarSlot, int score) {
	}
}
