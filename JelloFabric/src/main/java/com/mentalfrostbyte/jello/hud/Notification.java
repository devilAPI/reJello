package com.mentalfrostbyte.jello.hud;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.render.Fonts;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.util.Anim;
import com.mentalfrostbyte.jello.util.TimerUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

/** The slide-in toast at the top right. Same animation curve as the original. */
public class Notification {
	private final String text;
	private final String secondText;
	private final int time;
	private final TimerUtil timer = new TimerUtil();
	private boolean done;
	private boolean peaked;
	private float progress;
	private float lastProgress;
	private final @Nullable Identifier image;

	public Notification(String text, String secondText, int time, @Nullable Identifier image) {
		this.text = text;
		this.secondText = secondText;
		this.time = time;
		this.image = image;
		timer.reset();
	}

	public boolean isDone() {
		return done;
	}

	public void onTick() {
		lastProgress = progress;
		if (progress < 34 && !peaked) {
			progress += ((34 - progress) / 3) + 2;
		} else if (!peaked) {
			peaked = true;
		}
		if (progress > 0 && peaked && timer.hasTimeElapsed(time + 500, false)) {
			progress -= (progress / 3) + 2;
		} else if (progress <= 1 && peaked) {
			timer.reset();
			done = true;
		}
	}

	public float getProgress() {
		return Math.max(0, Anim.smooth(progress, lastProgress));
	}

	public void render(GuiGraphicsExtractor g) {
		if (done) {
			return;
		}
		JelloHud hud = Jello.hud;
		float animation = getProgress();
		float left = g.guiWidth() - animation * 5.264f + 3 + .5f;
		Render2D.gradient(g, left + 4.5f, 5f, left + 179 - 4.5f, 41 - 4.5f + .5f, hud.notificationTop(), hud.notificationBottom());
		Render2D.image(g, "notification", left, .5f, 179, 41);
		Render2D.image(g, "warning", left + 13, 14f, 13, 13.5f);
		if (image != null) {
			Render2D.image(g, image, left + 7, 7.5f, 27, 27, -1);
		}
		Fonts.jelloFontRegular.drawStringShadow(g, text, left + 4.5f + 32.5f + 4, 4.5f + 7.5f + 1, 0xccffffff);
		Fonts.jelloFontSmall.drawString(g, secondText, left + 4.5f + 32.5f + 4.5f, 4.5f + 19.5f + 1.5f, 0xaaffffff);
	}
}
