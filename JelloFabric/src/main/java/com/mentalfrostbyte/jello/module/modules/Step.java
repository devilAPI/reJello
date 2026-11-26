package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.EventType;
import com.mentalfrostbyte.jello.event.events.EventEntityStep;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

/** 1.8 Step: raises the step height to 1.5 and sends the matching position packets. */
public class Step extends Module {
	public static boolean enabled;
	private double preY;
	private boolean stepNextTick;

	public Step() {
		super("Step", 0, Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		enabled = true;
	}

	@Override
	public void onDisable() {
		enabled = false;
	}

	@EventTarget
	public void onStep(EventEntityStep event) {
		if (mc.player == null || event.getEntity() != mc.player) {
			return;
		}
		if (mc.player.isInWater() || mc.player.isInLava()) {
			return;
		}
		if (KillAura.attacking && Jello.isEnabled("Kill Aura")) {
			return;
		}
		if (event.getType() == EventType.PRE) {
			preY = mc.player.getY();
			if (canStep()) {
				event.setStepHeight(1.5f);
			}
		}
	}

	private boolean canStep() {
		LocalPlayer p = mc.player;
		return p != null && p.onGround() && p.getDeltaMovement().y < 0 && !p.input.keyPresses.jump();
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			return;
		}
		double offset = mc.player.getY() - preY;
		if (offset > 0.6 && !stepNextTick) {
			stepNextTick = true;
			if (mc.getConnection() != null) {
				double[] offsets = offset > 1.0 ? new double[] {0.42, 0.75, 1.0, 1.16, 1.23, 1.2} : new double[] {0.42, 0.75};
				for (double d : offsets) {
					mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(
						mc.player.getX(), preY + d, mc.player.getZ(), false, false));
				}
			}
		}
		if (stepNextTick && mc.player.onGround()) {
			stepNextTick = false;
			preY = mc.player.getY();
		}
	}
}
