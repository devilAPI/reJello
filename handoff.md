# Handoff: SigmaJello → Minecraft 26.3 (Fabric)

## Status

The port now compiles and runs (`./gradlew build`, `./gradlew runClient`). The original 1.8 source is kept
outside this repository as the public `EnderDrago0/SigmaJello` project; this repo contains the 26.3 port.

## Source facts

- The original is an **MCP 1.8** client, not 1.21. It uses asset index 1.8, `BlockAnvil`-style class names and bundles OptiFine 1.8.
- The Jello code is patched directly into vanilla classes (`net/minecraft/...`).
- The source also bundles third-party code: OptiFine 1.8, Xaero's Minimap and the JLayer MP3 decoder. None of it was carried over.

## Build setup (done)

| Item | Value |
|---|---|
| Minecraft | 26.3 (unobfuscated, Mojang names, no mappings) |
| Fabric Loader | 0.20.1 |
| Fabric API | 0.167.0+26.3 |
| Loom | `net.fabricmc.fabric-loom` 1.19-SNAPSHOT |
| Gradle wrapper | 9.8.2 |
| Java | 25 (the system JDK is 27) |

- `./gradlew genSources` has been run. The decompiled 26.3 sources were extracted to the session scratchpad, which is temporary. Regenerate them from `.gradle/loom-cache/minecraftMaven/.../minecraft-merged-*-26.3-sources.jar`.
- Mod metadata: `src/main/resources/fabric.mod.json`. It declares the client entrypoint `com.mentalfrostbyte.jello.JelloClient` (not written yet) and `jello.mixins.json` (not written yet).

## Assets (done)

- Textures: `assets/jello/textures/gui/*.png`, renamed to lowercase with spaces replaced by `_` (26.x identifiers require this). Each has a `.png.mcmeta` with `blur: true`, which replaces the old forced `GL_LINEAR` filtering.
- Fonts: `assets/jello/font/` holds `jellolight.ttf`, `jellomedium.ttf` and `jelloregular.ttf`, plus 16 vanilla TTF provider JSONs.
  - Each JSON sets `size` = the original CFont point size ÷ 2 and `oversample` = 4.
  - Each sets `shift.y` = 0.75·size − 7, which should make the draw y the top of the text.
  - This vertical offset has not been checked on screen.

## Code written (`src/main/java/com/mentalfrostbyte/jello/`)

| File | Contents |
|---|---|
| `event/*` | Annotation-based event bus (`EventManager`, `@EventTarget`) and event classes |
| `module/Module`, `module/Category` | Module base; the enable hook is deferred when no world is loaded and re-run by `onWorldJoin()`. Category enum with animation state |
| `setting/*` | `Value`, `BooleanValue`, `NumberValue` |
| `config/ConfigManager` | `Jello/Modules.txt` and `Jello/Settings.txt` in the original text format. Key codes are now SDL3 scancodes |
| `Jello.java` | Module registry, key dispatch, helpers. **Imports module classes that don't exist** |
| `render/Textures`, `render/JFont`, `render/Fonts` | Texture IDs and font wrappers on vanilla TTF fonts, with float positioning |
| `render/Render2D` | Float rects, gradients, textures with UV regions, circles, scissor; floats are done by scaling unit quads through the pose |
| `render/ColorSampler` | Asynchronous 1×1 framebuffer readback, used for the TabGUI and notification tints |
| `util/*` | Timer, interpolation, colour blend, player-movement helpers |
| `hud/*` | `JelloHud`, `Notification(Manager)`, `Compass`, `CircleManager` (keystroke ripples), `TabGUI`, `MiniMapRenderer` (replaces Xaero) |

## Code written

- `JelloClient` entrypoint, `jello.mixins.json` and the mixins (keyboard/mouse input, packet sent/received,
  movement, step height, item-use slowdown, camera no-clip, offline account accessor).
- All 22 module classes.
- ClickGUI (`gui/JelloGui`), panels and settings popup, closing animation.
- Chat command system (`command/CommandManager`: `.help`, `.bind`, `.toggle`, `.gui`, `.config`, `.alt`, ...).
- Offline alt manager (`alt/*`) persisting to `Jello/Alts.txt`.

## Not written

- Music panel and player (the original depended on a dead third-party API and convertmp3.io).
- Main menu replacement.
- Alt manager screen (the command interface works; there is no dedicated GUI yet).

## 26.3 API notes (verified against decompiled sources)

- **2D drawing:** `GuiGraphics` is now `GuiGraphicsExtractor`. Screens override `extractRenderState(g, mouseX, mouseY, a)`. The pose is a `Matrix3x2fStack`.
- **Screens:** they live on `minecraft.gui` (`gui.screen()`, `gui.setScreen()`).
- **Input events:** `mouseClicked(MouseButtonEvent, boolean)`, `keyPressed(KeyEvent)`, `charTyped(CharacterEvent)`.
- **Input codes:** input is SDL3. Key codes are scancodes (`InputConstants.KEY_A = 4`, `KEY_UP = 82`, `KEY_RSHIFT = 229`), and the left mouse button is 1.
- **Key mappings:** use Fabric's `KeyMappingHelper.getBoundKeyOf`.
- **Rendering backend:** renderpearl (Vulkan/OpenGL). There is no raw GL. For readback see `Screenshot.takeScreenshot` and `CommandEncoder.copyTextureToBuffer(..., x, y, w, h)`.
- **Fabric hooks:** `HudElementRegistry`, `ScreenEvents`, `ClientTickEvents`, `ClientPlayConnectionEvents`.
- **Inventory clicks:** `ClickType` is now `ContainerInput`; the call is `MultiPlayerGameMode.handleContainerInput`.
- **Player skins in GUIs:** `PlayerSkinWidget` / `GuiGraphicsExtractor.skin(...)` render a player model without a world.

## Notes on the original

- The original's YouTube music player depended on a hard-coded third-party API key and convertmp3.io, which has shut down.
- The alt manager used Mojang password login, which was retired in 2022.
- `getModule("Fast")` and `getModule("Rush")` refer to modules that never existed.
