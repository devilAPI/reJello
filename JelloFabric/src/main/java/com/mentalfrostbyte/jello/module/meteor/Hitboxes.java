package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.TridentItem;

/**
 * Meteor Client hitboxes: exposes an expansion value for entity hitboxes.
 *
 * <p>The renderer/raytrace only sees the size returned by {@code Entity#getBoundingBox()} /
 * {@code Entity#getPickRadius()}, so a mixin on {@code Entity} must call
 * {@link #getEntityValue(Entity)} for this module to have a visible effect.</p>
 */
public class Hitboxes extends Module {
	private final ModeValue targets = addValue(new ModeValue("Targets", "Players", "Players", "Mobs", "Both"));
	private final NumberValue expand = addValue(new NumberValue("Expand", 0.5, 0.0, 2.0, 0.1));
	private final BooleanValue ignoreFriends = addValue(new BooleanValue("Ignore Friends", true));
	private final BooleanValue onlyOnWeapon = addValue(new BooleanValue("Only On Weapon", false));
	private final BooleanValue sword = addValue(new BooleanValue("Sword", true));
	private final BooleanValue axe = addValue(new BooleanValue("Axe", true));
	private final BooleanValue pickaxe = addValue(new BooleanValue("Pickaxe", true));
	private final BooleanValue shovel = addValue(new BooleanValue("Shovel", true));
	private final BooleanValue hoe = addValue(new BooleanValue("Hoe", true));
	private final BooleanValue mace = addValue(new BooleanValue("Mace", true));
	private final BooleanValue spear = addValue(new BooleanValue("Spear", true));
	private final BooleanValue trident = addValue(new BooleanValue("Trident", true));

	public Hitboxes() {
		super("Hitboxes", 0, Category.COMBAT);
	}

	/** Expansion value for the given entity, or 0 when it should not be changed. */
	public double getExpand(Entity entity) {
		if (!isToggled() || entity == null || !testWeapon()) {
			return 0.0;
		}
		boolean player = entity instanceof Player;
		if (player) {
			if (targets.is("Mobs")) {
				return 0.0;
			}
			if (ignoreFriends.getValue() && mc.player != null && mc.player != entity
				&& mc.player.getTeam() != null && mc.player.getTeam() == ((Player) entity).getTeam()) {
				return 0.0;
			}
		} else if (targets.is("Players")) {
			return 0.0;
		}
		return expand.getValue();
	}

	/** Static entry point for an Entity mixin. */
	public static double getEntityValue(Entity entity) {
		for (Module module : Jello.mods) {
			if (module instanceof Hitboxes hitboxes && hitboxes.isToggled()) {
				return hitboxes.getExpand(entity);
			}
		}
		return 0.0;
	}

	private boolean testWeapon() {
		if (!onlyOnWeapon.getValue()) {
			return true;
		}
		if (mc.player == null) {
			return false;
		}
		ItemStack main = mc.player.getMainHandItem();
		if (main.isEmpty()) {
			return false;
		}
		if (sword.getValue() && main.is(ItemTags.SWORDS)) {
			return true;
		}
		if (axe.getValue() && main.is(ItemTags.AXES)) {
			return true;
		}
		if (pickaxe.getValue() && main.is(ItemTags.PICKAXES)) {
			return true;
		}
		if (shovel.getValue() && main.is(ItemTags.SHOVELS)) {
			return true;
		}
		if (hoe.getValue() && main.is(ItemTags.HOES)) {
			return true;
		}
		if (mace.getValue() && main.getItem() instanceof MaceItem) {
			return true;
		}
		if (spear.getValue() && main.is(ItemTags.SPEARS)) {
			return true;
		}
		return trident.getValue() && main.getItem() instanceof TridentItem;
	}
}
