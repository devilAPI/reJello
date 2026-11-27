package com.mentalfrostbyte.jello.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Accessors used by the alt manager, FastPlace and the module hooks. */
@Mixin(Minecraft.class)
public interface MinecraftAccessor {
	@Accessor("user")
	@Mutable
	void jello$setUser(User user);

	@Accessor("userApiService")
	@Mutable
	void jello$setUserApiService(UserApiService service);

	@Accessor("rightClickDelay")
	void jello$setRightClickDelay(int delay);
}
