# FreeWorld ![GitHub](https://img.shields.io/github/license/Over-Run/FreeWorld)

![GitHub issues](https://img.shields.io/github/issues-raw/Over-Run/FreeWorld)
![GitHub closed issues](https://img.shields.io/github/issues-closed-raw/Over-Run/FreeWorld)
![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/Over-Run/FreeWorld)
![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/Over-Run/FreeWorld?include_prereleases)
![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/Over-Run/FreeWorld)

**NOT OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG.**

This is a sandbox game.

## General Operations

The game will generate a world with size 64&times;64&times;64.  
//Fill with 2 layers of dirt and 1 layer of grass block.

- Keys
    - Escape: Save and Pause
    - W/A/S/D/Space/Left Shift: Move
    - 1: Choose grass block
    - 2: Choose dirt
    - F11: Switch fullscreen
- Mouse buttons
    - Left/Right: Destroy/Place

## JVM Properties

- freeworld.vsync=true
- freeworld.fps=60
- freeworld.tps=20

<!--## Command system

We added command in version 0.1.0-WIP.  
Just type the command in terminal.

- <a: type> : argument
- \[\<a: type>] : optional argument

Available commands:
- setblock <x: int> <y: int> <z: int> \[<block: string>]
- tp <x: float> <y: float> <z: float>

## Custom texture

You can customize your texture.  
The texture structure:
- 0, 0 ~ 15, 15: Top
- 16, 0 ~ 31, 15: Side
- 0, 16 ~ 15, 31: Bottom
- 16, 16, 31, 31: Side overlay-->

## Credits

- [LWJGL](https://www.lwjgl.org/) Core of game
    - [GLFW](https://www.glfw.org/)
    - [OpenGL](https://www.opengl.org/) Requires OpenGL 1.2: Don't upgrade to Windows 10 if you're using legacy Intel® Integrated Graphics
- [JOML](https://joml-ci.github.io/JOML/)
- [Mojang Studios](https://mojang.com/)
- [Log4j](http://logging.apache.org/log4j/2.x/index.html)
- [Minecrell](https://github.com/Minecrell/)
    - [LoggerNamePatternSelector](https://github.com/Minecrell/TerminalConsoleAppender/blob/master/src/main/java/net/minecrell/terminalconsole/util/LoggerNamePatternSelector.java)
