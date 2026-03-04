EscapeElytraMacer (Fabric + Kotlin, MC 1.21.8 / 1.21.11)
==============================================

✅ Mod Menu config screen
------------------------
Mod Menu discovers config screens via a `modmenu` entrypoint that implements `ModMenuApi`.
This project includes that entrypoint and returns a config Screen factory.

Settings are accessed from Fabric Mod Menu only.

Build & run (dev)
-----------------
- Install JDK 21
- In the project folder:
    ./gradlew runClient

Build jars for each target MC patch
-----------------------------------
- 1.21.11:
    ./gradlew build "-Pmod_version=1.2.0-mc1.21.11" "-Pminecraft_version=1.21.11" "-Pyarn_mappings=1.21.11+build.4" "-Pfabric_version=0.141.1+1.21.11"
- 1.21.8:
    ./gradlew build "-Pmod_version=1.2.0-mc1.21.8" "-Pminecraft_version=1.21.8" "-Pyarn_mappings=1.21.8+build.1" "-Pfabric_version=0.136.1+1.21.8"

Config file
-----------
Saved to:
  config/escapeelytramacer.json
