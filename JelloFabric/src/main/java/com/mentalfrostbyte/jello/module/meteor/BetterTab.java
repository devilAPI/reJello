package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.level.GameType;

/**
 * Meteor Client better tab. The tab-list size, column height, latency and name formatting are applied by a
 * {@code PlayerListHudMixin} in Meteor; that mixin is not present here, so this class keeps the settings and the
 * {@link #getPlayerName(PlayerInfo)} formatter for it. Friends highlighting is omitted (no friend system).
 */
public class BetterTab extends Module {
	private final NumberValue tabSize = addValue(new NumberValue("Tablist Size", 100, 1, 1000, 1));
	private final NumberValue tabHeight = addValue(new NumberValue("Column Height", 20, 1, 1000, 1));
	private final BooleanValue self = addValue(new BooleanValue("Highlight Self", true));
	private final BooleanValue accurateLatency = addValue(new BooleanValue("Accurate Latency", true));
	private final BooleanValue gamemode = addValue(new BooleanValue("Gamemode", false));

	public BetterTab() {
		super("Better Tab", 0, Category.RENDER);
	}

	public Component getPlayerName(PlayerInfo info) {
		Component name = info.getTabListDisplayName();
		if (name == null) {
			name = Component.literal(info.getProfile().name());
		}
		if (self.getValue() && mc.player != null && info.getProfile().id().equals(mc.player.getGameProfile().id())) {
			name = Component.literal(name.getString()).setStyle(name.getStyle().withColor(TextColor.fromRgb(0xFFA000)));
		}
		if (gamemode.getValue()) {
			GameType type = info.getGameMode();
			String text = type == null ? "?" : type.getShortDisplayName().getString();
			name = Component.literal("").append(name).append(" [" + text + "]");
		}
		return name;
	}

	public int getTabSize() {
		return (int) tabSize.getValue();
	}

	public int getColumnHeight() {
		return (int) tabHeight.getValue();
	}

	public boolean accurateLatency() {
		return accurateLatency.getValue();
	}
}
