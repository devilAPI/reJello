package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventAttack;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Wurst MaceDMG: sends fake fall-distance movement packets before a mace attack.
 *
 * <p>The server's movement handler only credits a limited amount of fall distance per packet, so
 * the same fake Y sequence as Wurst is sent (four times Y, once Y+sqrt(500), once Y again).</p>
 */
public class MaceDmg extends Module {
	public MaceDmg() {
		super("Mace Dmg", 0, Category.COMBAT);
	}

	@EventTarget
	public void onAttack(EventAttack event) {
		if (!isToggled() || mc.player == null) {
			return;
		}
		if (!(mc.hitResult instanceof EntityHitResult)) {
			return;
		}
		if (!mc.player.getMainHandItem().is(Items.MACE)) {
			return;
		}
		for (int i = 0; i < 4; i++) {
			sendFakeY(0);
		}
		sendFakeY(Math.sqrt(500));
		sendFakeY(0);
	}

	private void sendFakeY(double offset) {
		mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(),
			mc.player.getY() + offset, mc.player.getZ(), false, mc.player.horizontalCollision));
	}
}
