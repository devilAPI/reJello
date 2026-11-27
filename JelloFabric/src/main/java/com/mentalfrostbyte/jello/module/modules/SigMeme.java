package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import java.util.List;
import java.util.Random;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;

/** Sends a Sigma Reborn advert in chat after killing the player who last hit you. */
public class SigMeme extends Module {
	private final Random random = new Random();
	private final List<String> messages = List.of(
		"Learn your alphabet with the Sigma reborn client: Omikron, Sigma reborn, Epsilon, Alpha!",
		"Download Sigma reborn to kick ass while listening to some badass music!",
		"You have been oofed by Sigma reborn oof oof",
		"Sigma reborn never dies",
		"NoHaxJustSigmaReborn",
		"Sigma reborn is better than Optifine",
		"#SigmaRebornOnTop",
		"Get Sigma reborn noob!");
	private LivingEntity lastTarget;

	public SigMeme() {
		super("Sigmeme", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null) {
			return;
		}
		if (lastTarget != null && (lastTarget.isDeadOrDying() || lastTarget.isRemoved())) {
			p.connection.sendChat(messages.get(random.nextInt(messages.size())));
			lastTarget = null;
		}
		if (p.getLastHurtByMob() != null) {
			lastTarget = p.getLastHurtByMob();
		}
	}
}
