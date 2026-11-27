package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Meteor Client better chat. The send-side transformations (annoy/fancy/prefix/suffix/coords protection) and the
 * receive-side timestamps/filters use Fabric's message events, so no mixin is needed. Meteor's chat-HUD extras
 * (player heads, anti-spam counters, longer history, keep history) need {@code ChatHud}/{@code ChatComponent}
 * hooks and are not ported.
 */
public class BetterChat extends Module {
	private static final Pattern COORDS = Pattern.compile("(?<x>-?\\d{3,}(?:\\.\\d*)?)(?:\\s+(?<y>-?\\d{1,3}(?:\\.\\d*)?))?\\s+(?<z>-?\\d{3,}(?:\\.\\d*)?)");
	private static final Pattern ANTI_CLEAR = Pattern.compile("\\n(\\n|\\s)+\\n");
	private static final Map<Character, Character> SMALL_CAPS = new HashMap<>();

	static {
		String[] a = "abcdefghijklmnopqrstuvwxyz".split("");
		String[] b = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴩqʀꜱᴛᴜᴠᴡxyᴢ".split("");
		for (int i = 0; i < a.length; i++) {
			SMALL_CAPS.put(a[i].charAt(0), b[i].charAt(0));
		}
	}

	private final BooleanValue annoy = addValue(new BooleanValue("Annoy", false));
	private final BooleanValue fancy = addValue(new BooleanValue("Fancy Chat", false));
	private final BooleanValue timestamps = addValue(new BooleanValue("Timestamps", false));
	private final BooleanValue showSeconds = addValue(new BooleanValue("Show Seconds", false));
	private final BooleanValue coordsProtection = addValue(new BooleanValue("Coords Protection", true));
	private final BooleanValue antiSpam = addValue(new BooleanValue("Anti Spam", true));
	private final NumberValue antiSpamDepth = addValue(new NumberValue("Anti Spam Depth", 20, 1, 100, 1));
	private final BooleanValue antiClear = addValue(new BooleanValue("Anti Clear", true));
	private final BooleanValue prefix = addValue(new BooleanValue("Prefix", false));
	private final ModeValue prefixText = addValue(new ModeValue("Prefix Text", "> ", "> ", "[Jello] ", ""));
	private final BooleanValue suffix = addValue(new BooleanValue("Suffix", false));
	private final ModeValue suffixText = addValue(new ModeValue("Suffix Text", " | meteor on crack!", " | meteor on crack!", " | jello", ""));

	private final Deque<String> recent = new ArrayDeque<>();
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	private boolean lastSeconds;

	public BetterChat() {
		super("Better Chat", 0, Category.MISC);
		ClientSendMessageEvents.ALLOW_CHAT.register(this::onAllowSend);
		ClientSendMessageEvents.MODIFY_CHAT.register(this::onModifySend);
		ClientReceiveMessageEvents.ALLOW_GAME.register(this::onAllowGame);
		ClientReceiveMessageEvents.MODIFY_GAME.register(this::onModifyGame);
	}

	@Override
	public void onDisable() {
		recent.clear();
	}

	private boolean onAllowSend(String message) {
		if (!isToggled() || message == null || message.isEmpty() || message.startsWith("/") || message.startsWith(".")) {
			return true;
		}
		if (coordsProtection.getValue() && COORDS.matcher(message).find()) {
			Jello.addChatMessage("It looks like there are coordinates in your message! Send it again if you are sure.");
			return false;
		}
		return true;
	}

	private String onModifySend(String message) {
		if (!isToggled() || message == null || message.startsWith("/") || message.startsWith(".")) {
			return message;
		}
		if (annoy.getValue()) {
			message = annoy(message);
		}
		if (fancy.getValue()) {
			message = fancy(message);
		}
		String pre = prefix.getValue() ? prefixText.getMode() : "";
		String suf = suffix.getValue() ? suffixText.getMode() : "";
		return pre + message + suf;
	}

	private boolean onAllowGame(Component message, boolean overlay) {
		if (!isToggled() || overlay || !antiSpam.getValue()) {
			return true;
		}
		String text = message.getString();
		if (recent.contains(text)) {
			return false;
		}
		recent.addFirst(text);
		while (recent.size() > (int) antiSpamDepth.getValue()) {
			recent.removeLast();
		}
		return true;
	}

	private Component onModifyGame(Component message, boolean overlay) {
		if (!isToggled() || overlay) {
			return message;
		}
		String text = message.getString();
		if (antiClear.getValue() && ANTI_CLEAR.matcher(text).find()) {
			text = ANTI_CLEAR.matcher(text).replaceAll("\n\n");
			message = Component.literal(text);
		}
		if (timestamps.getValue()) {
			if (lastSeconds != showSeconds.getValue()) {
				lastSeconds = showSeconds.getValue();
				format = new SimpleDateFormat(lastSeconds ? "HH:mm:ss" : "HH:mm");
			}
			MutableComponent stamp = Component.literal("<" + format.format(new Date()) + "> ").withStyle(ChatFormatting.GRAY);
			message = Component.empty().append(stamp).append(message);
		}
		return message;
	}

	private static String annoy(String message) {
		StringBuilder sb = new StringBuilder(message.length());
		boolean upper = true;
		for (int cp : message.codePoints().toArray()) {
			sb.appendCodePoint(upper ? Character.toUpperCase(cp) : Character.toLowerCase(cp));
			upper = !upper;
		}
		return sb.toString();
	}

	private static String fancy(String message) {
		StringBuilder sb = new StringBuilder(message.length());
		for (char ch : message.toCharArray()) {
			sb.append(SMALL_CAPS.getOrDefault(ch, ch));
		}
		return sb.toString();
	}
}
