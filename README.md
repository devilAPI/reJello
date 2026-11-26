# reJello

SigmaJello (Minecraft 1.8 MCP client) ported to **Minecraft 26.3** on **Fabric**.

The port targets the unobfuscated 26.3 Mojang names, Fabric Loader 0.20.1 and Fabric API 0.167.0+26.3.
The `JelloFabric/` directory is the buildable mod. The original 1.8 source is **not** included; it is the
public [EnderDrago0/SigmaJello](https://github.com/EnderDrago0/SigmaJello) repository.

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

## Features

- Annotation based event bus and the original module list (22 modules: Kill Aura, Block Fly, Bunny Hop,
  Auto Armor, InvCleaner, Chest Stealer, NoFall, Fly, Step, NoSlow, AirWalk, LongJump, ...).
- Jello HUD: logo, TabGUI, KeyStrokes, mini map, compass, active-module list, notification toasts.
- ClickGUI with five category panels, dragging, scrolling and a settings popup (booleans and sliders).
- Dot commands in chat.
- TTF fonts and the original GUI textures.

## Commands

| Command | Description |
|---|---|
| `.help` | list commands |
| `.bind <module> <key>` | bind a module (e.g. `.bind Kill Aura X`) |
| `.unbind <module>` | clear a module's key |
| `.toggle <module>` | toggle a module |
| `.modules [category]` | list modules |
| `.enabled` | list enabled modules |
| `.settings <module>` | show a module's settings |
| `.set <module> <setting> <value>` | change a setting |
| `.gui` | open the ClickGUI |
| `.config save\|load` | save or load the module config |
| `.alt add\|list\|login\|remove` | manage offline profiles |

The default menu key is **right shift**.

## Notes

- Config is stored in `Jello/Modules.txt` and `Jello/Settings.txt` under the game directory.
- Account switching uses offline profiles (Mojang password login was retired in 2022); Microsoft accounts
  are handled by the launcher.
- The original music player depended on a third-party API key and convertmp3.io, both gone, so it is not ported.
