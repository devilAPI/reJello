package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Extends the entity/block interaction range attributes. */
public class Reach extends Module {
	private final NumberValue reach = addValue(new NumberValue("Reach", 4.0, 3.0, 6.0, 0.1));
	private double oldEntity = 3;
	private double oldBlock = 3;

	public Reach() {
		super("Reach", 0, Category.COMBAT);
	}

	@Override
	public void onEnable() {
		if (mc.player != null) {
			AttributeInstance entity = mc.player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
			AttributeInstance block = mc.player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
			if (entity != null) {
				oldEntity = entity.getBaseValue();
				entity.setBaseValue(reach.getValue());
			}
			if (block != null) {
				oldBlock = block.getBaseValue();
				block.setBaseValue(reach.getValue());
			}
		}
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		AttributeInstance entity = mc.player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
		AttributeInstance block = mc.player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
		if (entity != null) {
			entity.setBaseValue(reach.getValue());
		}
		if (block != null) {
			block.setBaseValue(reach.getValue());
		}
	}

	@Override
	public void onDisable() {
		if (mc.player != null) {
			AttributeInstance entity = mc.player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
			AttributeInstance block = mc.player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
			if (entity != null) {
				entity.setBaseValue(oldEntity);
			}
			if (block != null) {
				block.setBaseValue(oldBlock);
			}
		}
	}
}
