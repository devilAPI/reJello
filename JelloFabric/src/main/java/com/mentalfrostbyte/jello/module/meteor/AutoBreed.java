package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.EntityHitResult;

/** Meteor Client auto breed: feeds nearby animals that accept the held item. */
public class AutoBreed extends Module {
	private final NumberValue range = addValue(new NumberValue("Range", 4.5, 0, 32, 0.5));
	private final ModeValue hand = addValue(new ModeValue("Hand", "Main Hand", "Main Hand", "Off Hand"));
	private final ModeValue ageFilter = addValue(new ModeValue("Mob Age Filter", "Adult", "Adult", "Baby", "Both"));
	private final BooleanValue continuous = addValue(new BooleanValue("Continuous Breeding", false));
	private final NumberValue interval = addValue(new NumberValue("Breeding Interval", 6600, 1, 24000, 20));

	private final Map<Entity, Integer> animalsFed = new LinkedHashMap<>();
	private int tickCounter;

	public AutoBreed() {
		super("Auto Breed", 0, Category.WORLD);
	}

	@Override
	public void onEnable() {
		animalsFed.clear();
		tickCounter = 0;
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}

		InteractionHand breedingHand = hand.is("Off Hand") ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		ItemStack held = mc.player.getItemInHand(breedingHand);

		for (Entity entity : mc.level.entitiesForRendering()) {
			if (!(entity instanceof Animal animal) || !animal.isAlive()) {
				continue;
			}
			if (animalsFed.containsKey(animal) || !animal.isFood(held) || !ageAllowed(animal)) {
				continue;
			}
			if (animal.distanceToSqr(mc.player) > range.getValue() * range.getValue()) {
				continue;
			}

			mc.gameMode.interact(mc.player, animal,
				new EntityHitResult(animal, animal.getBoundingBox().getCenter()), breedingHand);
			mc.player.swing(breedingHand, SwingAnimation.DEFAULT, false);
			animalsFed.put(animal, tickCounter);
			break;
		}

		if (continuous.getValue()) {
			Iterator<Map.Entry<Entity, Integer>> it = animalsFed.entrySet().iterator();
			while (it.hasNext()) {
				if (it.next().getValue() < tickCounter - interval.getValue()) {
					it.remove();
				}
			}
			tickCounter++;
		}
	}

	private boolean ageAllowed(Animal animal) {
		return switch (ageFilter.getMode()) {
			case "Baby" -> animal.isBaby();
			case "Both" -> true;
			default -> !animal.isBaby();
		};
	}
}
