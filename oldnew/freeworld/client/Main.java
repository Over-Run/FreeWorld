/*
 * MIT License
 *
 * Copyright (c) 2021 OverRun Organization
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.overrun.freeworld.client;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.overrun.freeworld.block.Block;
import org.overrun.freeworld.block.Blocks;
import org.overrun.freeworld.client.render.Tesselator;
import org.overrun.freeworld.client.world.WorldRenderer;
import org.overrun.freeworld.entity.Player;
import org.overrun.freeworld.util.HitResult;
import org.overrun.freeworld.util.Identifier;
import org.overrun.freeworld.util.Timer;
import org.overrun.freeworld.util.Utils;
import org.overrun.freeworld.world.Chunk;
import org.overrun.freeworld.world.World;

import java.io.Closeable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.overrun.freeworld.FreeWorld.*;
import static org.overrun.freeworld.block.Blocks.*;
import static org.overrun.freeworld.client.Keyboard.isKeyDown;
import static org.overrun.freeworld.client.Window.getHeight;
import static org.overrun.freeworld.client.Window.getWidth;
import static org.overrun.freeworld.client.util.GLU.*;

/**
 * @author squid233
 * @since 2021/05/04
 */
public final class Main implements Runnable, Closeable {
    private static int lastMouseX, lastMouseY;
    private final FloatBuffer fogColor0 = memAllocFloat(4);
    private final FloatBuffer fogColor1 = memAllocFloat(4);
    private final IntBuffer viewportBuffer = memAllocInt(16);
    private final IntBuffer selectBuffer = memAllocInt(2000);
    private final FloatBuffer lightBuffer = memAllocFloat(16);
    private final Timer timer = new Timer(20);
    private boolean booted;
    private World world;
    private WorldRenderer worldRenderer;
    private Player player;
    private Block chosenBlock = GRASS_BLOCK;
    private HitResult hitResult;
    private long window;

    public static void main(String[] args) {
        try (Main main = new Main()) {
            main.start();
        }
    }

    public void start() {
        int col0 = 0xfefbfa;
        int col1 = 0xe0b0a;
        fogColor0.put(new float[]{
                (float) (col0 >> 16 & 255) / 255.0F,
                (float) (col0 >> 8 & 255) / 255.0F,
                (float) (col0 & 255) / 255.0F, 1.0F
        }).flip();
        fogColor1.put(new float[]{
                (float) (col1 >> 16 & 255) / 255.0F,
                (float) (col1 >> 8 & 255) / 255.0F,
                (float) (col1 & 255) / 255.0F, 1.0F
        }).flip();
        glfwSetErrorCallback((error, description) ->
                logger.error("GL Error {}: {}", error, memUTF8(description)));
        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        window = glfwCreateWindow(854,
                480,
                "FreeWorld " + VERSION,
                NULL,
                NULL);
        if (window == NULL) {
            throw new IllegalStateException("Failed to create the window");
        }
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                if (key == GLFW_KEY_ESCAPE) {
                    glfwSetWindowShouldClose(window, true);
                }
            }
        });
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            Window.width = Math.max(width, 1);
            Window.height = Math.max(height, 1);
        });
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidMode != null) {
                glfwSetWindowPos(window,
                        // (0 >> 1) -> (0 / 2)
                        (vidMode.width() - pWidth.get(0)) >> 1,
                        (vidMode.height() - pHeight.get(0)) >> 1
                );
            }
        }
        glfwMakeContextCurrent(window);
        glfwSwapInterval(V_SYNC ? 1 : 0);
        GL.createCapabilities();
        init();
        glfwShowWindow(window);
        booted = true;
        run();
    }

    public void init() {
        Blocks.init();
        glEnable(GL_TEXTURE_2D);
        glClearColor(0.4f, 0.6f, 0.9f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.5F);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        world = new World(256, 64, 256);
        worldRenderer = new WorldRenderer(world);
        player = new Player(world);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public void tick() {
        if (isKeyDown(GLFW_KEY_ENTER)) {
            world.save();
        }
        if (isKeyDown(GLFW_KEY_1)) {
            chosenBlock = GRASS_BLOCK;
        }
        if (isKeyDown(GLFW_KEY_2)) {
            chosenBlock = DIRT;
        }
        if (isKeyDown(GLFW_KEY_3)) {
            chosenBlock = COBBLESTONE;
        }
        world.tick();
        player.tick();
    }

    private void moveCameraToPlayer(float interval) {
        glTranslatef(0.0f, 0.0f, -0.3f);
        glRotatef(player.xRot, 1.0f, 0.0f, 0.0f);
        glRotatef(player.yRot, 0.0f, -1.0f, 0.0f);
        float x = player.xo + (player.x - player.xo) * interval;
        float y = player.yo + (player.y - player.yo) * interval;
        float z = player.zo + (player.z - player.zo) * interval;
        glTranslatef(-x, -y, -z);
    }

    private void setupCamera(float interval) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(70,
                (float) getWidth() / (float) getHeight(),
                0.05f,
                1000);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        moveCameraToPlayer(interval);
    }

    private void setupPickCamera(float interval, int x, int y) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glGetIntegerv(GL_VIEWPORT, viewportBuffer.clear());
        gluPickMatrix((float) x, (float) y, 1, 1, viewportBuffer.flip().limit(16));
        gluPerspective(70,
                (float) getWidth() / (float) getHeight(),
                0.05f,
                1000);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        moveCameraToPlayer(interval);
    }

    private void pick(float interval) {
        selectBuffer.clear();
        glSelectBuffer(selectBuffer);
        glRenderMode(GL_SELECT);
        setupPickCamera(interval, getWidth() / 2, getHeight() / 2);
        worldRenderer.pick(player);
        int hits = glRenderMode(GL_RENDER);
        selectBuffer.flip().limit(selectBuffer.capacity());
        long closest = 0;
        int[] names = new int[10];
        int hitNameCount = 0;
        for (int i = 0; i < hits; ++i) {
            int nameCount = selectBuffer.get();
            long minZ = selectBuffer.get();
            selectBuffer.get();
            int j;
            if (minZ >= closest && i != 0) {
                for (j = 0; j < nameCount; ++j) {
                    selectBuffer.get();
                }
            } else {
                closest = minZ;
                hitNameCount = nameCount;
                for (j = 0; j < nameCount; ++j) {
                    names[j] = selectBuffer.get();
                }
            }
        }
        if (hitNameCount > 0) {
            hitResult = new HitResult(names[1], names[2], names[3], names[4]);
        } else {
            hitResult = null;
        }
    }

    public void render(float interval) {
        int mouseX, mouseY;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var pX = stack.mallocDouble(1);
            var pY = stack.mallocDouble(1);
            glfwGetCursorPos(window, pX, pY);
            mouseX = (int) Math.floor(pX.get(0));
            mouseY = (int) Math.floor(pY.get(0));
        }
        int xo = lastMouseX - mouseX, yo = lastMouseY - mouseY;
        player.turn(xo, yo);
        pick(interval);
        if (hitResult != null) {
            if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
                world.setBlock(AIR, hitResult.x, hitResult.y, hitResult.z);
            } else if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS) {
                int x = hitResult.x;
                int y = hitResult.y;
                int z = hitResult.z;
                if (hitResult.face == RIGHT) {
                    x--;
                } else if (hitResult.face == LEFT) {
                    x++;
                } else if (hitResult.face == BOTTOM) {
                    y--;
                } else if (hitResult.face == TOP) {
                    y++;
                } else if (hitResult.face == BACK) {
                    z++;
                } else if (hitResult.face == FRONT) {
                    z--;
                }
                world.setBlock(chosenBlock, x, y, z);
            }
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        setupCamera(interval);
        glEnable(GL_CULL_FACE);
        worldRenderer.updateDirtyChunks(player);
        setupFog(0);
        glEnable(GL_FOG);
        worldRenderer.render(0);
        setupFog(1);
        worldRenderer.render(1);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_FOG);
        if (hitResult != null) {
            glDisable(GL_ALPHA_TEST);
            worldRenderer.renderHit(hitResult);
            glEnable(GL_ALPHA_TEST);
        }
        drawGui(interval);
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    private void drawGui(float interval) {
        int screenWidth = getWidth() * 240 / getHeight();
        int screenHeight = getHeight() * 240 / getHeight();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, screenWidth, screenHeight, 0, 100, 300);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        // 2x scale
        glTranslatef(0, 0, -200);
        glPushMatrix();
        glTranslatef(screenWidth - 16, 16, 0);
        Tesselator t = Tesselator.get();
        glScalef(16, 16, 16);
        glRotatef(30, 1, 0, 0);
        glRotatef(45, 0, 1, 0);
        glTranslatef(-1.5F, 0.5F, -0.5F);
        glScalef(-1, -1, 1);
        Texture.bind(new Identifier("textures/block/block_atlas.png"));
        glEnable(GL_TEXTURE_2D);
        chosenBlock.render(t.init(GL_QUADS), world, 0, -2, 0, 0).draw();
        glDisable(GL_TEXTURE_2D);
        glPopMatrix();
        int wc = screenWidth / 2;
        int hc = screenHeight / 2;
        glColor4f(1, 1, 1, 1);
        t.init(GL_QUADS)
         .vertex(wc + 1, hc - 4)
         .vertex(wc, hc - 4)
         .vertex(wc, hc + 5)
         .vertex(wc + 1, hc + 5)
         .vertex(wc + 5, hc)
         .vertex(wc - 4, hc)
         .vertex(wc - 4, hc + 1)
         .vertex(wc + 5, hc + 1)
         .draw();
    }

    private void setupFog(int layer) {
        if (layer == 0) {
            glFogi(GL_FOG_MODE, GL_EXP);
            glFogf(GL_FOG_DENSITY, 0.001f);
            glFogfv(GL_FOG_COLOR, fogColor0);
            glDisable(GL_LIGHTING);
        } else if (layer == 1) {
            glFogi(GL_FOG_MODE, GL_EXP);
            glFogf(GL_FOG_DENSITY, 0.06f);
            glFogfv(GL_FOG_COLOR, fogColor1);
            glEnable(GL_LIGHTING);
            glEnable(GL_COLOR_MATERIAL);
            float br = 0.6f;
            glLightModelfv(GL_LIGHT_MODEL_AMBIENT, getBuffer(br, br, br, 1));
        }
    }

    private FloatBuffer getBuffer(float r, float g, float b, float a) {
        return lightBuffer.clear().put(r).put(g).put(b).put(a).flip();
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        int frames = 0;
        while (!glfwWindowShouldClose(window)) {
            timer.advanceTime();
            for (int i = 0; i < timer.ticks; i++) {
                tick();
            }
            render(timer.interval);
            glfwSwapBuffers(window);
            glfwPollEvents();
            ++frames;
            while (System.currentTimeMillis() >= (lastTime + 1000)) {
                glfwSetWindowTitle(window, "FreeWorld " +
                        VERSION +
                        ", " +
                        frames +
                        " fps (" +
                        player.x +
                        ", " +
                        player.y +
                        ", " +
                        player.z +
                        ")");
                Chunk.updates = 0;
                lastTime += 1000;
                frames = 0;
            }
        }
    }

    @Override
    public void close() {
        Utils.memFree(fogColor0,
                fogColor1,
                viewportBuffer,
                selectBuffer,
                lightBuffer);
        free();
        if (booted) {
            world.save();
            Texture.clear();
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            glfwTerminate();
        }
    }
}
