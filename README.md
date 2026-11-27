# reJello

SigmaJello ported to **Minecraft 26.3** on **Fabric**, combining the original 1.8 MCP client with the
**Sigma Reborn 1.18** module set.

The port targets the unobfuscated 26.3 Mojang names, Fabric Loader 0.20.1 and Fabric API 0.167.0+26.3.
The buildable mod is in `JelloFabric/`. The original 1.8 source is the public
[EnderDrago0/SigmaJello](https://github.com/EnderDrago0/SigmaJello) repository; the Reborn 1.18 jar is
decompiled with Vineflower and its module list is what the 72 registered modules below come from.

## Build

```sh
cd JelloFabric
./gradlew build
```

The jar is written to `JelloFabric/build/libs/`. Requires Java 25 or newer.

## Run (development)

```sh
cd JelloFabric
./gradlew runClient
```

## Modules (72)

**Movement** AirWalk, BlockFly, BunnyHop, ClickTP, Eagle, FastSneak, Fly, HighJump, InvMove, JelloBot,
LongJump, NoSlow, Phase, Speed, Sprint, Step

**Player** AntiAtlas, AntiCheat, AntiVoid, AutoRegister, AutoTool, Blink, Derp, Disabler, FastUse, NoFall,
ServerCrasher, Sigmeme

**Combat** AimAssist, AntiBot, AntiKnockback, AutoClicker, Criticals, HvHAura, InfiniteAura, KillAura,
Reach, Regen, TargetStrafe

**Item** AutoArmor, AutoPot, ChestStealer, InvCleaner, InvManager

**Render** ActiveMods, Animations, Camera No Clip, Chams, ClickGUI, Compass, ESP, Freecam, FullBright,
ItemPhysics, KeyStrokes, MiniMap, NameProtect, NoHurtCam, Notifications, TabGUI, TargetHUD, Tracers,
WorldTime, Test

**World** FastPlace, Nuker, Timer

**Misc** AntiCrash, AutoPlay, NoRotateSet, ResetVL, RiderGod, idk

The ClickGUI, HUD (logo, TabGUI, KeyStrokes, mini map, compass, active-module list, notifications),
TTF fonts, dot commands, mode/boolean/number settings and the offline alt manager are included.

## Commands

| Command | Description |
|---|---|
| `.help` | list commands |
| `.bind <module> <key>` | bind a module (e.g. `.bind KillAura R`) |
| `.unbind <module>` | clear a module's key |
| `.toggle <module>` | toggle a module |
| `.modules [category]` | list modules |
| `.enabled` | list enabled modules |
| `.settings <module>` | show a module's settings |
| `.set <module> <setting> <value>` | change a setting (e.g. `.set KillAura Rotation Mode Lock`) |
| `.gui` | open the ClickGUI |
| `.config save\|load` | save or load the module config |
| `.alt add\|list\|login\|remove` | manage offline profiles |

`Menu` is hidden from the category panels and also opens the ClickGUI with **right shift**.

## Porting notes

- Settings: `BooleanValue`, `NumberValue` and the Reborn `ModeValue` (cycle dropdown) are supported and
  persisted in `Jello/Modules.txt` / `Jello/Settings.txt`.
- The ClickGUI uses the Reborn look: `JelloPanelNew`/`JelloPanelDark` panels, a Light/Dark theme,
Center/Right module text, the blue selection colour, the panel overlay and background particles, laid out
on a fixed virtual canvas in a 4+3 grid so all seven categories stay on screen. Theme, blur, particles and
text position are controlled by the `ClickGUI` module's settings.
- Modules that depend on 1.8/1.18 packet exploits or server-specific anti-cheat behaviour (Disabler,
  ServerCrasher, AutoPlay, JelloBot, Timer, Freecam, Chams, ItemPhysics, Animations) are registered with
  their settings and categories but are intentionally not given a destructive implementation.
- Account switching uses offline profiles (Mojang password login was retired in 2022); Microsoft accounts
  are handled by the launcher.
- The original music player depended on a third-party API key and convertmp3.io, both gone, so it is not ported.
