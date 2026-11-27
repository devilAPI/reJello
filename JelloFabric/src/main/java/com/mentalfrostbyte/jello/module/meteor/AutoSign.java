package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Meteor Client auto sign: reuses the text of the first sign you fill in.
 * Meteor cancels the screen-open event; without a screen mixin the screen is closed again after the
 * packet was sent, which still lets the server accept the text.
 */
public class AutoSign extends Module {
	private final NumberValue delay = addValue(new NumberValue("Delay", 10, 0, 100, 1));

	private List<String> text;
	private SignTextSlot slot = SignTextSlot.FRONT;
	private BlockPos lastPos;
	private BlockPos targetPos;
	private int phase;
	private int timer;
	private boolean ignoreNextPacket;

	public AutoSign() {
		super("Auto Sign", 0, Category.WORLD);
	}

	@Override
	public void onDisable() {
		text = null;
		lastPos = null;
		targetPos = null;
		phase = 0;
		timer = 0;
	}

	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (event.getPacket() instanceof ServerboundSignUpdatePacket packet) {
			if (ignoreNextPacket) {
				ignoreNextPacket = false;
				return;
			}
			text = packet.lines();
			slot = packet.slot();
			lastPos = packet.pos();
		}
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.player == null || mc.gui.screen() == null) {
			phase = 0;
			timer = 0;
			return;
		}
		if (!(mc.gui.screen() instanceof AbstractSignEditScreen)) {
			phase = 0;
			timer = 0;
			return;
		}

		if (phase == 0) {
			// The first sign is written by the player; only later signs are automated.
			if (text == null) {
				return;
			}
			targetPos = mc.hitResult instanceof BlockHitResult hit ? hit.getBlockPos() : lastPos;
			phase = 1;
			timer = 0;
			return;
		}

		if (phase == 1) {
			if (timer++ < (int) delay.getValue()) {
				return;
			}
			if (targetPos != null && text != null && mc.getConnection() != null) {
				mc.getConnection().send(new ServerboundSignUpdatePacket(targetPos, text, slot));
			}
			phase = 2;
			timer = 0;
			return;
		}

		// Give the asynchronous sign-text filter on the server a moment before the screen removes itself.
		if (timer++ >= 10) {
			ignoreNextPacket = true;
			mc.gui.setScreen(null);
			ignoreNextPacket = false;
			phase = 0;
			timer = 0;
		}
	}
}
