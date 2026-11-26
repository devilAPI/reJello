package com.mentalfrostbyte.jello.hud;

import com.mentalfrostbyte.jello.Jello;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import java.util.List;

/** Arrow-key category / module navigator state. Rendering lives in {@link JelloHud}. */
public class TabGUI {
	public final List<Category> cats = List.of(Category.values());
	public int currentCategory;
	public float seenTrans;
	public boolean showModules;

	private List<Module> current() {
		return Jello.getModulesInCategory(cats.get(currentCategory));
	}

	public void keyRight() {
		if (!showModules) {
			showModules = true;
		} else if (!current().isEmpty()) {
			current().get(cats.get(currentCategory).selectedIndex).toggle();
		}
	}

	public void keyLeft() {
		showModules = false;
	}

	public void keyDown() {
		Category c = cats.get(currentCategory);
		if (!showModules) {
			currentCategory = currentCategory < cats.size() - 1 ? currentCategory + 1 : 0;
		} else {
			c.selectedIndex = c.selectedIndex < current().size() - 1 ? c.selectedIndex + 1 : 0;
		}
	}

	public void keyUp() {
		Category c = cats.get(currentCategory);
		if (!showModules) {
			currentCategory = currentCategory > 0 ? currentCategory - 1 : cats.size() - 1;
		} else {
			c.selectedIndex = c.selectedIndex > 0 ? c.selectedIndex - 1 : current().size() - 1;
		}
	}
}
