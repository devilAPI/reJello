package com.mentalfrostbyte.jello.module;

import com.mentalfrostbyte.jello.event.EventManager;
import com.mentalfrostbyte.jello.hud.NotificationManager;
import com.mentalfrostbyte.jello.setting.Value;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;

public abstract class Module {
	protected static final Minecraft mc = Minecraft.getInstance();

	private final String name;
	private String displayName;
	/** {@link com.mojang.blaze3d.platform.InputConstants} key value, 0 = unbound. */
	public int keyCode;
	private boolean toggled;
	protected final List<Value> values = new ArrayList<>();
	/** Null for modules that are hidden from the TabGUI / ClickGUI (Notifications, Menu). */
	public final @Nullable Category category;

	public float animation;
	public float hoverPercent;
	public float lastHoverPercent;

	protected Module(String name, int keyCode, @Nullable Category category) {
		this.name = name;
		this.displayName = name;
		this.keyCode = keyCode;
		this.category = category;
	}

	public void toggle() {
		setToggled(!toggled);
		if (mc.player != null) {
			NotificationManager.notify(toggled ? "Enabled" : "Disabled", getDisplayName(), 250);
		}
	}

	/** Changes state, running enable/disable hooks, without the notification. */
	public void setToggled(boolean state) {
		if (state == toggled) {
			return;
		}
		toggled = state;
		if (toggled) {
			EventManager.register(this);
			if (mc.player != null && mc.level != null) {
				onEnable();
			}
		} else {
			EventManager.unregister(this);
			if (mc.player != null && mc.level != null) {
				onDisable();
			}
		}
	}

	/** Re-runs the enable hook for modules that were switched on outside a world (config load, main menu). */
	public void onWorldJoin() {
		if (toggled) {
			onEnable();
		}
	}

	public void onEnable() {
	}

	public void onDisable() {
	}

	/** Called every local-player living update (EntityPlayerSP.onLivingUpdate in 1.8). */
	public void onUpdate() {
	}

	public boolean isToggled() {
		return toggled;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	protected <T extends Value> T addValue(T value) {
		values.add(value);
		return value;
	}

	public List<Value> getValues() {
		return values;
	}

	public @Nullable Value getValue(String label) {
		for (Value v : values) {
			if (v.getName().equalsIgnoreCase(label)) {
				return v;
			}
		}
		return null;
	}
}
