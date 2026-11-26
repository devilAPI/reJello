package com.mentalfrostbyte.jello.render;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;

/** Resolves original "Jello/Name.png" texture names to {@code jello:textures/gui/name.png}. */
public final class Textures {
	private static final Map<String, Identifier> CACHE = new HashMap<>();

	private Textures() {
	}

	public static Identifier get(String name) {
		return CACHE.computeIfAbsent(name, n -> Identifier.fromNamespaceAndPath("jello",
			"textures/gui/" + n.toLowerCase().replace(' ', '_') + ".png"));
	}
}
