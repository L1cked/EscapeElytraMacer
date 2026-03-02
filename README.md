EscapeElytraMacer (Fabric + Kotlin, MC 1.21.11)
==============================================

✅ Mod Menu config screen
------------------------
Mod Menu discovers config screens via a `modmenu` entrypoint that implements `ModMenuApi`.
This project includes that entrypoint and returns a config Screen factory.

Also includes a client command:
  /escapeelytramacer config
  /escapeelytramacer reload

Build & run (dev)
-----------------
- Install JDK 21
- In the project folder:
    ./gradlew runClient

Config file
-----------
Saved to:
  config/escapeelytramacer.json
