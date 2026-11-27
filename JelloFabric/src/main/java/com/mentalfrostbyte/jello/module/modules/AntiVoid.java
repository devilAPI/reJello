package com.mentalfrostbyte.jello.module.modules;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import net.minecraft.client.player.LocalPlayer;

/** Catches the player before falling into the void. */
public class AntiVoid extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "TPBack", "TPBack", "MovementFlag", "Jump"));
	private final BooleanValue checkVoid = addValue(new BooleanValue("VoidCheck", true));
	private final NumberValue jumpMotion = addValue(new NumberValue("Jump Motion", 3.0, 2.5, 3.5, 0.5));
	private double lastX;
	private double lastY;
	private double lastZ;

	public AntiVoid() {
		super("AntiVoid", 0, Category.PLAYER);
	}

	@Override
	public void onUpdate() {
		LocalPlayer p = mc.player;
		if (!isToggled() || p == null) {
			return;
		}
		if (p.onGround()) {
			lastX = p.getX();
			lastY = p.getY();
			lastZ = p.getZ();
		}
		boolean falling = !p.onGround() && p.getDeltaMovement().y < 0;
		if (!falling) {
			return;
		}
		if (checkVoid.getValue() && p.getY() > mc.level.getMinY() + 5) {
			return;
		}
		switch (mode.getMode()) {
			case "Jump" -> p.setDeltaMovement(p.getDeltaMovement().x, jumpMotion.getValue() / 100.0, p.getDeltaMovement().z);
			case "MovementFlag" -> p.setOnGround(true);
			default -> {
				p.setPos(lastX, lastY, lastZ);
				p.setDeltaMovement(0, 0, 0);
			}
		}
	}
}
