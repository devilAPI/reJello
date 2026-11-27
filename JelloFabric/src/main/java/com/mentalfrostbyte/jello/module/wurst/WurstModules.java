package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.Jello;

/** Registers the Wurst-exclusive hacks that are not covered by the Sigma/Meteor module set. */
public final class WurstModules {
	private WurstModules() {
	}

	public static void registerAll() {
		Jello.register(new AutoBuild());
		Jello.register(new AutoFarm());
		Jello.register(new AutoSoup());
		Jello.register(new AutoSwim());
		Jello.register(new BoatFly());
		Jello.register(new BonemealAura());
		Jello.register(new BuildRandom());
		Jello.register(new CaveFinder());
		Jello.register(new ChatTranslator());
		Jello.register(new CrashChest());
		Jello.register(new FeedAura());
		Jello.register(new FightBot());
		Jello.register(new Follow());
		Jello.register(new HandNoClip());
		Jello.register(new HeadRoll());
		Jello.register(new HealthTags());
		Jello.register(new InfiniChat());
		Jello.register(new InstaBuild());
		Jello.register(new InstantBunker());
		Jello.register(new ItemGenerator());
		Jello.register(new Kaboom());
		Jello.register(new Liquids());
		Jello.register(new Lsd());
		Jello.register(new MaceDmg());
		Jello.register(new MassTpa());
		Jello.register(new MileyCyrus());
		Jello.register(new MobSpawnEsp());
		Jello.register(new MultiAura());
		Jello.register(new Navigator());
		Jello.register(new Restock());
	}
}
