package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

/**
 * Meteor Client auto-reconnect: returns to the last server after the disconnect screen appears.
 * The "hide buttons"/countdown label of Meteor lives in a {@code DisconnectedScreen} mixin, which is not part of
 * this build; reconnection itself is driven from the tick loop instead.
 */
public class AutoReconnect extends Module {
	private final NumberValue delay = addValue(new NumberValue("Delay", 3.5, 0, 100, 0.1));
	private final BooleanValue hideButtons = addValue(new BooleanValue("Hide Buttons", false));
	private final TimerUtil timer = new TimerUtil();
	private ServerData lastServer;

	public AutoReconnect() {
		super("Auto Reconnect", 0, Category.MISC);
	}

	@Override
	public void onEnable() {
		timer.reset();
	}

	@Override
	public void onUpdate() {
		if (mc.player != null && mc.getCurrentServer() != null) {
			lastServer = mc.getCurrentServer();
		}
		if (lastServer == null || mc.gui.screen() == null || !(mc.gui.screen() instanceof DisconnectedScreen)) {
			return;
		}
		if (timer.hasTimeElapsed((long) (delay.getValue() * 1000.0), true)) {
			ConnectScreen.startConnecting(new TitleScreen(), mc, ServerAddress.parseString(lastServer.ip), lastServer, false, null);
		}
	}

	public ServerData getLastServer() {
		return lastServer;
	}

	public boolean hideButtons() {
		return hideButtons.getValue();
	}
}
