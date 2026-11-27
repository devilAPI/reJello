package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventTick;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.NumberValue;
import java.util.Map;
import net.minecraft.world.clock.ClockNetworkState;

/**
 * Meteor Client TimeChanger: overrides the client-side clock time.
 * Ported against the 26.3 clock manager (the same API WorldTime predates). Server time updates are applied
 * between ticks because cancelling {@code ClientboundSetTimePacket} would need a new packet hook.
 */
public class TimeChanger extends Module {
	private final NumberValue time = addValue(new NumberValue("Time", 0, -20000, 20000, 100));

	private long oldTime;

	public TimeChanger() {
		super("Time Changer", 0, Category.RENDER);
	}

	@Override
	public void onEnable() {
		if (mc.level != null) {
			oldTime = mc.level.getDefaultClockTime();
		}
	}

	@Override
	public void onDisable() {
		if (mc.level == null) {
			return;
		}
		long restore = oldTime;
		mc.level.dimensionType().defaultClock().ifPresent(clock -> {
			mc.level.clockManager().handleUpdates(mc.level.getGameTime(),
				Map.of(clock, new ClockNetworkState(restore, 0f, 1f)));
			mc.level.environmentAttributes().invalidateTickCache();
		});
	}

	@EventTarget
	public void onTick(EventTick event) {
		if (!isToggled() || mc.level == null) {
			return;
		}
		long custom = (long) time.getValue();
		mc.level.dimensionType().defaultClock().ifPresent(clock -> {
			mc.level.clockManager().handleUpdates(mc.level.getGameTime(),
				Map.of(clock, new ClockNetworkState(custom, 0f, 0f)));
			mc.level.environmentAttributes().invalidateTickCache();
		});
	}
}
