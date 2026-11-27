package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

/** Wurst MassTpa: sends a teleport request to every player on the server. */
public class MassTpa extends Module {
	private final ModeValue command = addValue(new ModeValue("Command", "/tpa",
		"/tpa", "/tp", "/tpahere", "/tpo"));
	private final NumberValue delay = addValue(new NumberValue("Delay", 20, 1, 200, 1));
	private final BooleanValue ignoreErrors = addValue(new BooleanValue("Ignore Errors", false));
	private final BooleanValue stopWhenAccepted = addValue(new BooleanValue("Stop When Accepted", true));

	private final ArrayList<String> players = new ArrayList<>();
	private final Random random = new Random();
	private int index;
	private int timer;

	public MassTpa() {
		super("Mass Tpa", 0, Category.MISC);
		ClientReceiveMessageEvents.MODIFY_GAME.register(this::onReceive);
	}

	@Override
	public void onEnable() {
		players.clear();
		index = 0;
		timer = 0;
		if (mc.getConnection() == null || mc.getUser() == null) {
			return;
		}
		String self = mc.getUser().getName();
		for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
			String name = info.getProfile().name();
			if (name != null && !name.equalsIgnoreCase(self)) {
				players.add(name);
			}
		}
		Collections.shuffle(players, random);
		if (players.isEmpty()) {
			Jello.addChatMessage("Couldn't find any players.");
			setToggled(false);
		}
	}

	@Override
	public void onUpdate() {
		if (!isToggled() || mc.getConnection() == null) {
			return;
		}
		if (timer > 0) {
			timer--;
			return;
		}
		if (index >= players.size()) {
			setToggled(false);
			return;
		}
		String cmd = command.getMode().substring(1);
		mc.getConnection().sendCommand(cmd + " " + players.get(index));
		index++;
		timer = Math.max(1, (int) delay.getValue()) - 1;
	}

	private Component onReceive(Component message, boolean overlay) {
		if (!isToggled() || overlay) {
			return message;
		}
		String text = message.getString().toLowerCase();
		if (text.contains("jello")) {
			return message;
		}
		if (text.contains("/help") || text.contains("permission")) {
			if (!ignoreErrors.getValue()) {
				Jello.addChatMessage("This server doesn't have a " + command.getMode().toUpperCase() + " command.");
				setToggled(false);
			}
			return message;
		}
		if (stopWhenAccepted.getValue()
			&& (text.contains("accepted") && text.contains("request")
				|| text.contains("akzeptiert") && text.contains("anfrage"))) {
			Jello.addChatMessage("Someone accepted your " + command.getMode().toUpperCase() + " request. Stopping.");
			setToggled(false);
		}
		return message;
	}
}
