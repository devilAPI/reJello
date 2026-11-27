package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.util.PlayerUtil;
import net.minecraft.client.player.LocalPlayer;

/** Wall running: holds the player against a wall while jumping sideways. */
public class Idk extends Module {
	private float speed;

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null) {
			return;
		}
		boolean side = mc.options.keyLeft.isDown() || mc.options.keyRight.isDown();
		if (p.horizontalCollision && side && !mc.options.keyJump.isDown()) {
			speed = Math.min(0.8f, speed + 0.05f);
			double[] motion = PlayerUtil.motionFor(speed);
			p.setDeltaMovement(motion[0], -0.02, motion[1]);
		} else if (p.onGround()) {
			speed = 0;
		}
	}

	public Idk() {
		super("idk", 0, Category.MISC);
	}
}
