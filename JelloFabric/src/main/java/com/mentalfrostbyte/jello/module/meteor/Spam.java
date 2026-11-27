package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.Random;
import net.minecraft.client.gui.screens.DisconnectedScreen;

/**
 * Meteor Client spam. Messages come from a preset list because the settings system has no string-list setting yet.
 */
public class Spam extends Module {
	private final ModeValue message = addValue(new ModeValue("Message", "Meteor on Crack!",
		"Meteor on Crack!", "Jello on top!", "GG"));
	private final NumberValue delay = addValue(new NumberValue("Delay", 20, 0, 200, 1));
	private final BooleanValue disableOnLeave = addValue(new BooleanValue("Disable On Leave", true));
	private final BooleanValue disableOnDisconnect = addValue(new BooleanValue("Disable On Disconnect", true));
	private final BooleanValue random = addValue(new BooleanValue("Randomise", false));
	private final BooleanValue autoSplit = addValue(new BooleanValue("Auto Split Messages", false));
	private final NumberValue splitLength = addValue(new NumberValue("Split Length", 256, 1, 256, 1));
	private final NumberValue splitDelay = addValue(new NumberValue("Split Delay", 20, 0, 200, 1));
	private final BooleanValue bypass = addValue(new BooleanValue("Bypass", false));
	private final BooleanValue uppercase = addValue(new BooleanValue("Include Uppercase", true));
	private final NumberValue length = addValue(new NumberValue("Bypass Length", 16, 1, 64, 1));

	private final Random rng = new Random();
	private int messageIndex;
	private int timer;
	private int splitNum;
	private String text;

	public Spam() {
		super("Spam", 0, Category.MISC);
	}

	@Override
	public void onEnable() {
		timer = (int) delay.getValue();
		messageIndex = 0;
		splitNum = 0;
		text = null;
	}

	@Override
	public void onDisable() {
		text = null;
	}

	@Override
	public void onUpdate() {
		if (mc.player == null) {
			if (disableOnLeave.getValue()) {
				setToggled(false);
			}
			return;
		}
		if (mc.gui.screen() instanceof DisconnectedScreen) {
			if (disableOnDisconnect.getValue()) {
				setToggled(false);
			}
			return;
		}
		if (timer > 0) {
			timer--;
			return;
		}
		if (text == null) {
			text = pickMessage();
			if (bypass.getValue()) {
				text += " " + randomText((int) length.getValue(), uppercase.getValue());
			}
		}
		if (autoSplit.getValue() && text.length() > splitLength.getValue()) {
			int size = (int) splitLength.getValue();
			int start = splitNum * size;
			int end = Math.min(start + size, text.length());
			mc.player.connection.sendChat(text.substring(start, end));
			int splits = (int) Math.ceil(text.length() / (double) size);
			splitNum = (splitNum + 1) % splits;
			timer = (int) splitDelay.getValue();
			if (splitNum == 0) {
				timer = (int) delay.getValue();
				text = null;
			}
		} else {
			mc.player.connection.sendChat(text.length() > 256 ? text.substring(0, 256) : text);
			timer = (int) delay.getValue();
			text = null;
		}
	}

	private String pickMessage() {
		var modes = message.getModes();
		if (modes.isEmpty()) {
			return message.getMode();
		}
		if (random.getValue()) {
			return modes.get(rng.nextInt(modes.size()));
		}
		if (messageIndex >= modes.size()) {
			messageIndex = 0;
		}
		return modes.get(messageIndex++);
	}

	private String randomText(int size, boolean withUpperCase) {
		String chars = withUpperCase ? "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" : "abcdefghijklmnopqrstuvwxyz";
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			sb.append(chars.charAt(rng.nextInt(chars.length())));
		}
		return sb.toString();
	}
}
