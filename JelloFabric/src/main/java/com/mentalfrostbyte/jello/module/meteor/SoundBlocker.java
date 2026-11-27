package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.ModeValue;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;

/**
 * Meteor Client sound blocker. There is no sound-play event or {@code SoundEngine} mixin in this build yet, so the
 * settings and the {@link #shouldBlock(SoundInstance)} predicate are provided for one to call.
 */
public class SoundBlocker extends Module {
	private final ModeValue source = addValue(new ModeValue("Source", "None",
		"None", "Blocks", "Players", "Weather", "Music", "Ambient", "UI", "All"));

	public SoundBlocker() {
		super("Sound Blocker", 0, Category.MISC);
	}

	public boolean shouldBlock(SoundInstance sound) {
		if (!isToggled() || sound == null) {
			return false;
		}
		return switch (source.getMode()) {
			case "All" -> true;
			case "Blocks" -> sound.getSource() == SoundSource.BLOCKS;
			case "Players" -> sound.getSource() == SoundSource.PLAYERS;
			case "Weather" -> sound.getSource() == SoundSource.WEATHER;
			case "Music" -> sound.getSource() == SoundSource.MUSIC || sound.getSource() == SoundSource.RECORDS;
			case "Ambient" -> sound.getSource() == SoundSource.AMBIENT;
			case "UI" -> sound.getSource() == SoundSource.UI;
			default -> false;
		};
	}

	public boolean shouldBlock(Identifier id) {
		if (!isToggled() || id == null) {
			return false;
		}
		String path = id.getPath();
		return switch (source.getMode()) {
			case "All" -> true;
			case "Blocks" -> path.startsWith("block.");
			case "Players" -> path.startsWith("entity.player.");
			case "Weather" -> path.startsWith("weather.");
			case "Music" -> path.startsWith("music.") || path.startsWith("record.");
			case "Ambient" -> path.startsWith("ambient.");
			case "UI" -> path.startsWith("ui.");
			default -> false;
		};
	}
}
