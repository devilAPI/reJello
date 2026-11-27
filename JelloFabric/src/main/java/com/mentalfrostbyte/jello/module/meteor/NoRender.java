package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;

/** Meteor Client module (No Render). Behaviour port pending. */
public class NoRender extends Module {
	private final BooleanValue value1 = addValue(new BooleanValue("portal-overlay", false));
	private final BooleanValue value2 = addValue(new BooleanValue("spyglass-overlay", false));
	private final BooleanValue value3 = addValue(new BooleanValue("nausea", false));
	private final BooleanValue value4 = addValue(new BooleanValue("pumpkin-overlay", false));
	private final BooleanValue value5 = addValue(new BooleanValue("powdered-snow-overlay", false));
	private final BooleanValue value6 = addValue(new BooleanValue("fire-overlay", false));
	private final BooleanValue value7 = addValue(new BooleanValue("liquid-overlay", false));
	private final BooleanValue value8 = addValue(new BooleanValue("in-wall-overlay", false));
	private final BooleanValue value9 = addValue(new BooleanValue("vignette", false));
	private final BooleanValue value10 = addValue(new BooleanValue("gui-background", false));
	private final BooleanValue value11 = addValue(new BooleanValue("totem-animation", false));
	private final BooleanValue value12 = addValue(new BooleanValue("eating-particles", false));
	private final BooleanValue value13 = addValue(new BooleanValue("enchantment-glint", false));
	private final BooleanValue value14 = addValue(new BooleanValue("boss-bar", false));
	private final BooleanValue value15 = addValue(new BooleanValue("scoreboard", false));
	private final BooleanValue value16 = addValue(new BooleanValue("crosshair", false));
	private final BooleanValue value17 = addValue(new BooleanValue("title", false));
	private final BooleanValue value18 = addValue(new BooleanValue("held-item-name", false));
	private final BooleanValue value19 = addValue(new BooleanValue("obfuscation", false));
	private final BooleanValue value20 = addValue(new BooleanValue("potion-icons", false));
	private final BooleanValue value21 = addValue(new BooleanValue("message-signature-indicator", false));
	private final BooleanValue value22 = addValue(new BooleanValue("weather", false));
	private final BooleanValue value23 = addValue(new BooleanValue("world-border", false));
	private final BooleanValue value24 = addValue(new BooleanValue("blindness", false));
	private final BooleanValue value25 = addValue(new BooleanValue("darkness", false));
	private final BooleanValue value26 = addValue(new BooleanValue("fog", false));
	private final BooleanValue value27 = addValue(new BooleanValue("enchantment-table-book", false));
	private final BooleanValue value28 = addValue(new BooleanValue("sign-text", false));
	private final BooleanValue value29 = addValue(new BooleanValue("block-break-overlay", false));
	private final BooleanValue value30 = addValue(new BooleanValue("beacon-beams", false));
	private final BooleanValue value31 = addValue(new BooleanValue("falling-blocks", false));
	private final BooleanValue value32 = addValue(new BooleanValue("cave-culling", false));
	private final BooleanValue value33 = addValue(new BooleanValue("map-markers", false));
	private final BooleanValue value34 = addValue(new BooleanValue("map-contents", false));
	private final ModeValue value35 = addValue(new ModeValue("banners", "Everything", "Everything", "E", "P", "N"));
	private final BooleanValue value36 = addValue(new BooleanValue("firework-explosions", false));
	private final BooleanValue value37 = addValue(new BooleanValue("barrier-invisibility", false));
	private final BooleanValue value38 = addValue(new BooleanValue("texture-rotations", false));
	private final BooleanValue value39 = addValue(new BooleanValue("drop-spawn-packets", false));
	private final BooleanValue value40 = addValue(new BooleanValue("armor", false));
	private final BooleanValue value41 = addValue(new BooleanValue("invisibility", false));
	private final BooleanValue value42 = addValue(new BooleanValue("glowing", false));
	private final BooleanValue value43 = addValue(new BooleanValue("spawner-entities", false));
	private final BooleanValue value44 = addValue(new BooleanValue("dead-entities", false));
	private final BooleanValue value45 = addValue(new BooleanValue("nametags", false));

	public NoRender() {
		super("No Render", 0, Category.RENDER);
	}
}
