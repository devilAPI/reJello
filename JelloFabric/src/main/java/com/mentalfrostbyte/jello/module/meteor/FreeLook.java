package com.mentalfrostbyte.jello.module.meteor;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.events.EventTick;
import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.setting.BooleanValue;
import com.mentalfrostbyte.jello.setting.ModeValue;
import com.mentalfrostbyte.jello.setting.NumberValue;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.CameraType;
import net.minecraft.util.Mth;

/**
 * Meteor Client FreeLook: rotates the camera independently of the player in third person.
 *
 * <p>The perspective toggle and the arrow-key rotation of the player are ported. Rotating the camera without
 * turning the player needs Meteor's {@code Camera} mixin (it feeds {@code cameraYaw}/{@code cameraPitch} into
 * the camera rotation), which Jello does not have yet: {@link #cameraYaw} and {@link #cameraPitch} are
 * maintained here for that hook.</p>
 */
public class FreeLook extends Module {
	private final ModeValue mode = addValue(new ModeValue("Mode", "Player", "Player", "Camera"));
	private final BooleanValue togglePerspective = addValue(new BooleanValue("Toggle Perspective", true));
	private final NumberValue cameraSensitivity = addValue(new NumberValue("Camera Sensitivity", 8, 0, 10, 0.5));
	private final BooleanValue arrows = addValue(new BooleanValue("Arrows Control Opposite", true));
	private final NumberValue arrowSpeed = addValue(new NumberValue("Arrow Speed", 4, 0, 10, 0.5));

	public float cameraYaw;
	public float cameraPitch;
	private CameraType previousPerspective;

	public FreeLook() {
		super("Free Look", 0, Category.RENDER);
	}

	@Override
	public void onEnable() {
		if (mc.player == null) {
			return;
		}
		cameraYaw = mc.player.getYRot();
		cameraPitch = mc.player.getXRot();
		previousPerspective = mc.options.getCameraType();

		if (togglePerspective.getValue() && previousPerspective != CameraType.THIRD_PERSON_BACK) {
			mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
		}
	}

	@Override
	public void onDisable() {
		if (previousPerspective != null && togglePerspective.getValue()
			&& mc.options.getCameraType() != previousPerspective) {
			mc.options.setCameraType(previousPerspective);
		}
	}

	/** True when the camera should be decoupled from the player (needs a Camera mixin to take effect). */
	public boolean playerMode() {
		return isToggled() && mc.options.getCameraType() == CameraType.THIRD_PERSON_BACK && mode.is("Player");
	}

	/** True when the player should be rotated instead of the camera. */
	public boolean cameraMode() {
		return isToggled() && mode.is("Camera");
	}

	public double cameraSensitivity() {
		return cameraSensitivity.getValue();
	}

	@EventTarget
	public void onTick(EventTick event) {
		if (!isToggled() || mc.player == null) {
			return;
		}

		if (arrows.getValue()) {
			int steps = (int) (arrowSpeed.getValue() * 2);
			for (int i = 0; i < steps; i++) {
				if (mode.is("Player")) {
					if (InputConstants.isKeyDown(InputConstants.KEY_LEFT)) cameraYaw -= 0.5f;
					if (InputConstants.isKeyDown(InputConstants.KEY_RIGHT)) cameraYaw += 0.5f;
					if (InputConstants.isKeyDown(InputConstants.KEY_UP)) cameraPitch -= 0.5f;
					if (InputConstants.isKeyDown(InputConstants.KEY_DOWN)) cameraPitch += 0.5f;
				} else {
					float yaw = mc.player.getYRot();
					float pitch = mc.player.getXRot();
					if (InputConstants.isKeyDown(InputConstants.KEY_LEFT)) yaw -= 0.5f;
					if (InputConstants.isKeyDown(InputConstants.KEY_RIGHT)) yaw += 0.5f;
					if (InputConstants.isKeyDown(InputConstants.KEY_UP)) pitch -= 0.5f;
					if (InputConstants.isKeyDown(InputConstants.KEY_DOWN)) pitch += 0.5f;
					mc.player.setYRot(yaw);
					mc.player.setXRot(pitch);
				}
			}
		}

		mc.player.setXRot(Mth.clamp(mc.player.getXRot(), -90, 90));
		cameraPitch = Mth.clamp(cameraPitch, -90, 90);
	}
}
