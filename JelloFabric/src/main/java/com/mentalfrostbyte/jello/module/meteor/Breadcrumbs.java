package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventRender2D;
import com.mentalfrostbyte.jello.event.events.EventTick;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.render.Render2D;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Meteor Client Breadcrumbs: stores walked path sections and draws them as a trail. */
public class Breadcrumbs extends Module {
	private record Section(Vec3 from, Vec3 to) {
	}

	private final NumberValue maxSections = addValue(new NumberValue("Max Sections", 1000, 1, 5000, 50));
	private final NumberValue sectionLength = addValue(new NumberValue("Section Length", 0.5, 0.0, 2.0, 0.1));
	private final ModeValue color = addValue(new ModeValue("Color", "Red", "Red", "Cyan", "Green", "White"));

	private final Deque<Section> sections = new ArrayDeque<>();
	private Vec3 last;
	private ResourceKey<Level> dimension;

	public Breadcrumbs() {
		super("Breadcrumbs", 0, Category.RENDER);
	}

	@Override
	public void onEnable() {
		clear();
		if (mc.player != null && mc.level != null) {
			last = mc.player.position();
			dimension = mc.level.dimension();
		}
	}

	@Override
	public void onDisable() {
		clear();
	}

	@EventTarget
	public void onTick(EventTick event) {
		if (!isToggled() || mc.player == null || mc.level == null) {
			return;
		}

		Vec3 current = mc.player.position();
		if (dimension != mc.level.dimension()) {
			clear();
			dimension = mc.level.dimension();
			last = current;
			return;
		}
		if (last == null) {
			last = current;
			return;
		}

		double length = sectionLength.getValue();
		if (Math.abs(current.x - last.x) < length
			&& Math.abs(current.y - last.y) < length
			&& Math.abs(current.z - last.z) < length) {
			return;
		}

		sections.addLast(new Section(last, current));
		while (sections.size() > (int) maxSections.getValue()) {
			sections.removeFirst();
		}
		last = current;
	}

	@EventTarget
	public void onRender(EventRender2D event) {
		if (!isToggled() || sections.isEmpty()) {
			return;
		}
		int lineColor = switch (color.getMode()) {
			case "Cyan" -> 0xFF19E1E1;
			case "Green" -> 0xFF19E119;
			case "White" -> 0xFFFFFFFF;
			default -> 0xFFE11919;
		};

		for (Section section : sections) {
			float[] from = project(event, section.from());
			float[] to = project(event, section.to());
			if (from == null || to == null) {
				continue;
			}
			Render2D.line(event.graphics, from[0], from[1], to[0], to[1], 1.6f, lineColor);
		}
	}

	private void clear() {
		sections.clear();
		last = null;
	}

	private float[] project(EventRender2D event, Vec3 point) {
		Vec3 p = mc.gameRenderer.projectPointToScreen(point);
		if (p.z < -1 || p.z > 1 || Double.isNaN(p.x) || Double.isNaN(p.y)) {
			return null;
		}
		return new float[]{
			(float) ((p.x * 0.5 + 0.5) * event.graphics.guiWidth()),
			(float) ((-p.y * 0.5 + 0.5) * event.graphics.guiHeight())
		};
	}
}
