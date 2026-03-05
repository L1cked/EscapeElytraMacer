# EscapeElytraMacer

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11%20%7C%201.21.8-3C8527?logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Loader-Fabric-orange)](https://fabricmc.net/)
[![License: MIT](https://img.shields.io/github/license/L1cked/EscapeElytraMacer)](https://github.com/L1cked/EscapeElytraMacer/blob/main/LICENSE)
[![Release](https://img.shields.io/github/v/release/L1cked/EscapeElytraMacer?display_name=tag)](https://github.com/L1cked/EscapeElytraMacer/releases)

A client-side Fabric mod that disconnects you when a nearby player is holding a mace and moving at dangerous speed.

## Why Use It

On high-risk servers, mace burst damage can happen before you can react. EscapeElytraMacer adds a lightweight panic safeguard by monitoring nearby players and instantly disconnecting when configured danger conditions are met.

## Key Features

- Client-only behavior (no server plugin required)
- Automatic disconnect on configurable threat detection
- Optional falling-only detection mode to reduce false positives
- Cooldown support to prevent repeated triggers
- In-game settings screen through Mod Menu integration

## Compatibility

- Minecraft: `1.21.11` (default)
- Also buildable for: `1.21.8`
- Loader: Fabric Loader `>= 0.18.1`
- Java: `21`
- Fabric API: required
- Fabric Language Kotlin: required
- Mod Menu: optional (recommended for in-game config UI)

## Installation

1. Install Fabric Loader for your Minecraft version.
2. Place the built `.jar` in your Minecraft `mods` folder.
3. Ensure Fabric API is installed.
4. Launch the game.

## Configuration

- Preferred: open Mod Menu and configure EscapeElytraMacer in-game.
- File-based config path: `config/escapeelytramacer.json`

### Main Settings

- `enabled`: Master on/off toggle
- `radiusBlocks`: Detection radius around your player
- `speedThreshold`: Minimum movement speed to trigger protection
- `requireFalling`: If enabled, only triggers when the target is descending
- `downYThreshold`: Vertical speed threshold for descending checks
- `checkEveryTicks`: Scan frequency (1 = every tick)
- `cooldownTicks`: Delay before another trigger can occur

## Development

### Run In Dev Client

```bash
./gradlew runClient
```

### Build (Default Version)

```bash
./gradlew build
```

### Build for Specific Minecraft Patches

For Minecraft `1.21.11`:

```bash
./gradlew build "-Pmod_version=1.2.0-mc1.21.11" "-Pminecraft_version=1.21.11" "-Pyarn_mappings=1.21.11+build.4" "-Pfabric_version=0.141.1+1.21.11"
```

For Minecraft `1.21.8`:

```bash
./gradlew build "-Pmod_version=1.2.0-mc1.21.8" "-Pminecraft_version=1.21.8" "-Pyarn_mappings=1.21.8+build.1" "-Pfabric_version=0.136.1+1.21.8"
```

## License

MIT License. See `LICENSE` for details.
