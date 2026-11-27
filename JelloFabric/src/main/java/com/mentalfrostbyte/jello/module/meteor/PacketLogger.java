package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventPacketSent;
import com.mentalfrostbyte.jello.event.events.EventReceivePacket;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.protocol.Packet;

/**
 * Meteor Client packet logger. Logs outgoing packets and the incoming types the packet mixin forwards, with optional
 * chat/file output, per-type counters and a summary on disable. The full S2C list needs a generic packet hook.
 */
public class PacketLogger extends Module {
	private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

	private final BooleanValue showTimestamp = addValue(new BooleanValue("Show Timestamp", true));
	private final BooleanValue showPacketData = addValue(new BooleanValue("Show Packet Data", false));
	private final BooleanValue showCount = addValue(new BooleanValue("Show Count", true));
	private final BooleanValue showSummary = addValue(new BooleanValue("Show Summary", true));
	private final BooleanValue logToChat = addValue(new BooleanValue("Log To Chat", true));
	private final BooleanValue logToFile = addValue(new BooleanValue("Log To File", false));
	private final ModeValue direction = addValue(new ModeValue("Direction", "Both", "Both", "C2S", "S2C"));

	private final Map<String, Integer> counts = new HashMap<>();
	private BufferedWriter writer;

	public PacketLogger() {
		super("Packet Logger", 0, Category.MISC);
	}

	@Override
	public void onEnable() {
		counts.clear();
		closeWriter();
		if (!logToFile.getValue()) {
			return;
		}
		try {
			Path dir = Jello.getDirectory().resolve("packet-logs");
			Files.createDirectories(dir);
			String name = "packets-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".log";
			writer = Files.newBufferedWriter(dir.resolve(name), StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} catch (IOException e) {
			Jello.addChatMessage("Packet Logger: could not open log file: " + e.getMessage());
		}
	}

	@Override
	public void onDisable() {
		if (showSummary.getValue() && !counts.isEmpty()) {
			line("--- SUMMARY ---");
			counts.entrySet().stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
				.forEach(e -> line("  " + e.getKey() + ": " + e.getValue()));
		}
		closeWriter();
		counts.clear();
	}

	@EventTarget
	public void onPacketSent(EventPacketSent event) {
		if (isToggled() && !direction.is("S2C")) {
			logPacket(event.getPacket(), "-> C2S");
		}
	}

	@EventTarget
	public void onPacketReceived(EventReceivePacket event) {
		if (isToggled() && !direction.is("C2S")) {
			logPacket(event.getPacket(), "<- S2C");
		}
	}

	private void logPacket(Packet<?> packet, String dir) {
		String type = packet.type().toString();
		int count = counts.merge(type, 1, Integer::sum);
		StringBuilder msg = new StringBuilder();
		if (showTimestamp.getValue()) {
			msg.append('[').append(LocalDateTime.now().format(TIME)).append("] ");
		}
		msg.append(dir).append(' ').append(type);
		if (showCount.getValue()) {
			msg.append(" (#").append(count).append(')');
		}
		if (showPacketData.getValue()) {
			msg.append(" Data: ").append(packet);
		}
		line(msg.toString());
	}

	private void line(String text) {
		if (logToChat.getValue()) {
			Jello.addChatMessage(text);
		}
		if (writer != null) {
			try {
				writer.write(text);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				Jello.addChatMessage("Packet Logger: write failed: " + e.getMessage());
				closeWriter();
			}
		}
	}

	private void closeWriter() {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException ignored) {
			}
			writer = null;
		}
	}
}
