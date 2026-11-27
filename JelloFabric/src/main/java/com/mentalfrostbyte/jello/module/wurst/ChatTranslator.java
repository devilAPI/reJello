package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.network.chat.Component;

/**
 * Wurst ChatTranslator: translates received and/or sent chat through Google Translate.
 *
 * <p>Uses Fabric's message events (like BetterChat) and the same translate.google.com/m endpoint as
 * Wurst. No mixin is needed; the request runs on a virtual thread and the result is handed back to
 * the client thread with {@code Minecraft.execute}.</p>
 */
public class ChatTranslator extends Module {
	private static final Pattern RESULT = Pattern.compile("class=\"result-container\">([^<]*)</div>");
	private static final HttpClient HTTP = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(5)).build();

	private final ModeValue whatToTranslate = addValue(new ModeValue("Translate", "Both", "Both", "Received", "Sent"));
	private final ModeValue yourLanguage = addValue(new ModeValue("Your Language", "English",
		"English", "German", "French", "Spanish", "Italian", "Portuguese", "Russian", "Chinese",
		"Japanese", "Korean", "Turkish", "Polish", "Dutch"));
	private final ModeValue otherLanguage = addValue(new ModeValue("Other Language", "Chinese",
		"English", "German", "French", "Spanish", "Italian", "Portuguese", "Russian", "Chinese",
		"Japanese", "Korean", "Turkish", "Polish", "Dutch"));
	private final BooleanValue detectReceived = addValue(new BooleanValue("Detect Received", true));

	private boolean sendingTranslation;

	public ChatTranslator() {
		super("Chat Translator", 0, Category.MISC);
		ClientReceiveMessageEvents.MODIFY_GAME.register(this::onReceive);
		ClientSendMessageEvents.ALLOW_CHAT.register(this::onAllowSend);
	}

	private Component onReceive(Component message, boolean overlay) {
		if (!isToggled() || overlay || !(whatToTranslate.is("Both") || whatToTranslate.is("Received"))) {
			return message;
		}
		String text = message.getString();
		if (text.isBlank()) {
			return message;
		}
		String self = mc.getUser() == null ? null : mc.getUser().getName();
		if (self != null && text.contains(self)) {
			return message;
		}
		String from = detectReceived.getValue() ? "auto" : code(otherLanguage.getMode());
		String to = code(yourLanguage.getMode());
		Thread.ofVirtual().name("JelloChatTranslator").start(() -> {
			String translated = translate(text, from, to);
			if (translated != null) {
				mc.execute(() -> {
					if (!isToggled()) {
						return;
					}
					if (mc.gui != null && mc.gui.hud != null) {
						mc.gui.hud.getChat().addClientSystemMessage(
							Component.literal("[" + otherLanguage.getMode() + " -> " + yourLanguage.getMode() + "] "
								+ translated));
					}
				});
			}
		});
		return message;
	}

	private boolean onAllowSend(String message) {
		if (!isToggled() || sendingTranslation || message == null || message.isEmpty()
			|| !(whatToTranslate.is("Both") || whatToTranslate.is("Sent"))) {
			return true;
		}
		if (message.startsWith("/") || message.startsWith(".")) {
			return true;
		}
		sendingTranslation = true;
		Thread.ofVirtual().name("JelloChatTranslator").start(() -> {
			String translated = translate(message, code(yourLanguage.getMode()), code(otherLanguage.getMode()));
			if (translated == null) {
				translated = message;
			}
			String result = translated;
			mc.execute(() -> {
				try {
					if (mc.player != null) {
						mc.player.connection.sendChat(result);
					}
				} finally {
					sendingTranslation = false;
				}
			});
		});
		return false;
	}

	private static String translate(String text, String from, String to) {
		try {
			String url = "https://translate.google.com/m?hl=en&sl=" + from + "&tl=" + to + "&ie=UTF-8&prev=_m&q="
				+ URLEncoder.encode(text.trim(), StandardCharsets.UTF_8);
			HttpRequest request = HttpRequest.newBuilder(URI.create(url))
				.timeout(Duration.ofSeconds(10))
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11")
				.GET().build();
			HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				return null;
			}
			Matcher matcher = RESULT.matcher(response.body());
			if (!matcher.find()) {
				return null;
			}
			String translated = unescape(matcher.group(1));
			return translated.isBlank() || translated.equalsIgnoreCase(text.trim()) ? null : translated;
		} catch (Exception e) {
			Jello.LOGGER.warn("ChatTranslator request failed", e);
			return null;
		}
	}

	private static String unescape(String text) {
		return text.replace("&quot;", "\"").replace("&#39;", "'").replace("&lt;", "<")
			.replace("&gt;", ">").replace("&amp;", "&");
	}

	private static String code(String language) {
		return switch (language) {
			case "German" -> "de";
			case "French" -> "fr";
			case "Spanish" -> "es";
			case "Italian" -> "it";
			case "Portuguese" -> "pt";
			case "Russian" -> "ru";
			case "Chinese" -> "zh-CN";
			case "Japanese" -> "ja";
			case "Korean" -> "ko";
			case "Turkish" -> "tr";
			case "Polish" -> "pl";
			case "Dutch" -> "nl";
			default -> "en";
		};
	}
}
