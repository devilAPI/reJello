package com.mentalfrostbyte.jello.hud;

import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.util.Anim;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Ripple circles shown inside a KeyStrokes key when it is pressed. */
public class CircleManager {
	private static final class Circle {
		final float x, y, topRadius, speed;
		final KeyMapping key;
		final boolean mouse;
		float progress, lastProgress;
		boolean complete;

		Circle(float x, float y, float topRadius, float speed, KeyMapping key, boolean mouse) {
			this.x = x;
			this.y = y;
			this.topRadius = topRadius;
			this.speed = speed;
			this.key = key;
			this.mouse = mouse;
		}
	}

	private final List<Circle> circles = new ArrayList<>();

	public void addCircle(float x, float y, float radius, float speed, KeyMapping key, boolean mouse) {
		circles.add(new Circle(x, y, radius, speed, key, mouse));
	}

	public void runCircles() {
		circles.removeIf(c -> c.complete);
		for (Circle c : circles) {
			c.lastProgress = c.progress;
			// Keyboard ripples hold at two thirds while the key stays down.
			if (!c.mouse && c.progress > c.topRadius * 0.67 && c.key.isDown()) {
				continue;
			}
			c.progress += (c.topRadius - c.progress) / c.speed + 0.01f;
			if (c.progress >= c.topRadius) {
				c.complete = true;
			}
		}
	}

	public void drawCircles(GuiGraphicsExtractor g, float yOffset) {
		for (Circle c : circles) {
			if (c.complete) {
				continue;
			}
			float progress = Anim.smooth(c.progress, c.lastProgress);
			float alpha = (1 - Math.min(1f, Math.max(0f, progress / c.topRadius))) / 2;
			Render2D.circle(g, (int) c.x, (int) (c.y + yOffset), progress, Render2D.white(alpha));
		}
	}
}
