package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;

/** Wurst HeadRoll: nods the sent head pitch up and down. */
public class HeadRoll extends Module {
	private final NumberValue speed = addValue(new NumberValue("Speed", 10, 1, 20, 1));

	private float pitch;
	private boolean nodding;

	public HeadRoll() {
		super("Head Roll", 0, Category.MISC);
	}

	@Override
	public void onDisable() {
		nodding = false;
		if (mc.player != null) {
			pitch = mc.player.getXRot();
		}
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null) {
			nodding = false;
			return;
		}
		nodding = true;
		int period = 21 - (int) speed.getValue();
		float timer = mc.player.tickCount % Math.max(2, period * 2) / (float) Math.max(1, period);
		pitch = Mth.sin(timer * (float) Math.PI) * 90F;
	}

	/** Sends the nod through every movement packet so the server and other players see it. */
	@EventTarget
	public void onPacket(EventPacketSent event) {
		if (!nodding || mc.player == null || !(event.getPacket() instanceof ServerboundMovePlayerPacket)) {
			return;
		}
		event.setPacket(new ServerboundMovePlayerPacket.PosRot(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
			mc.player.getYRot(), pitch, mc.player.onGround(), mc.player.horizontalCollision));
	}
}
