package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

/** Wurst Kaboom: instantly breaks everything around the player with an explosion effect. */
public class Kaboom extends Module {
	private final NumberValue power = addValue(new NumberValue("Power", 128, 32, 512, 32));
	private final BooleanValue sound = addValue(new BooleanValue("Sound", true));
	private final BooleanValue particles = addValue(new BooleanValue("Particles", true));

	public Kaboom() {
		super("Kaboom", 0, Category.WORLD);
	}

	@Override
	public void onEnable() {
		if (mc.player == null || mc.level == null || mc.gameMode == null) {
			return;
		}
		if (!mc.player.getAbilities().instabuild && !mc.player.onGround()) {
			setToggled(false);
			return;
		}
		double x = mc.player.getX();
		double y = mc.player.getY();
		double z = mc.player.getZ();
		if (sound.getValue()) {
			float pitch = (1F + (mc.player.getRandom().nextFloat() - mc.player.getRandom().nextFloat()) * 0.2F) * 0.7F;
			mc.level.playLocalSound(x, y, z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4, pitch, false);
		}
		if (particles.getValue()) {
			mc.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0, 0);
		}
		List<BlockPos> blocks = getBlocks();
		for (int i = 0; i < (int) power.getValue() && i < blocks.size(); i++) {
			mc.gameMode.destroyBlock(blocks.get(i));
		}
		setToggled(false);
	}

	private List<BlockPos> getBlocks() {
		Vec3 eyes = mc.player.getEyePosition();
		BlockPos origin = BlockPos.containing(eyes);
		List<BlockPos> blocks = new ArrayList<>();
		for (int x = -6; x <= 6; x++) {
			for (int y = -6; y <= 6; y++) {
				for (int z = -6; z <= 6; z++) {
					BlockPos pos = origin.offset(x, y, z);
					if (Vec3.atCenterOf(pos).distanceToSqr(eyes) <= 36) {
						blocks.add(pos.immutable());
					}
				}
			}
		}
		blocks.sort(Comparator.comparingDouble((BlockPos pos) ->
			-Vec3.atCenterOf(pos).distanceToSqr(eyes)));
		return blocks;
	}
}
