# FreeWorld

This is a sandbox game.

**WARNING: LOW PERFORMANCE**

## General Operations

The game will generate a world with size 32\*64\*32.  
Fill with 4 layers of dirt and 1 layer of grass block.

- Keys
    - Escape: Exit game
    - Grave Accent (~): Switch cursor mode
    - W/A/S/D/Space/Left Shift: Move
    - 1: Choose grass block
    - 2: Choose dirt
- Mouse buttons
    - Left/Right: Destroy/Place
    
## JVM Properties

- freeworld.vsync
- freeworld.fps
- freeworld.ups

## Custom texture

You can customize your texture.  
The texture structure:
    - 0, 0 ~ 15, 15: Top
    - 16, 0 ~ 31, 15: Side
    - 0, 16 ~ 15, 31: Bottom
    - 16, 16, 31, 31: Side overlay

## Credits

- [LWJGL](https://www.lwjgl.org/) Core of game
    - [GLFW](https://www.glfw.org/)
    - [OpenGL](https://www.opengl.org/) Requires OpenGL 2.0: Don't upgrade to Windows 10 if you're using legacy Intel® Integrated Graphics
    - Assimp: Unused currently
- JOML
- [Mojang Studios](https://mojang.com)
    - For textures
- Log4j
- [Minecrell](https://github.com/Minecrell/)
    - The Kotlin version of `io.github.overrun.freeworld.util.LoggerNamePatternSelector`
