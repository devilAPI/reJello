package com.mentalfrostbyte.jello.module;

/** The five Jello categories (TabGUI.Cat in the original), with the TabGUI/ClickGUI animation state. */
public enum Category {
	MOVEMENT("Movement", "movementcat"),
	PLAYER("Player", "playercat"),
	COMBAT("Combat", "combatcat"),
	ITEM("Item", "itemcat"),
	RENDER("Render", "rendercat");

	public final String name;
	public final String texture;
	public int selectedIndex;
	public float selectedTrans;
	public float lastSelectedTrans;
	public float seenTrans;

	Category(String name, String texture) {
		this.name = name;
		this.texture = texture;
	}
}
