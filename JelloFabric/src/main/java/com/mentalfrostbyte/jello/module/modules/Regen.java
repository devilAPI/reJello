package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

/** Health regeneration by re-sending position confirmations. */
public class Regen extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Vanilla", "Vanilla", "Timer"));
	private final NumberValue repeat = addValue(new NumberValue("RepeatTimes", 1, 1, 60, 1));
	private final TimerUtil timer = new TimerUtil();

	public Regen() {
		super("Regen", 0, Category.COMBAT);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null || mc.getConnection() == null) {
			return;
		}
		if (p.getHealth() < p.getMaxHealth() && timer.hasTimeElapsed(50, true)) {
			for (int i = 0; i <= (int) repeat.getValue(); i++) {
				mc.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(p.onGround(), p.horizontalCollision));
			}
		}
	}
}
