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

package org.overrun.freeworld.client.render;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 2021/05/05
 */
public class Tesselator {
    private static final Tesselator INSTANCE = new Tesselator();
    private static final int MAX_MEMORY_USE = 4 * 1024 * 1024;
    private static final int MAX_FLOATS = MAX_MEMORY_USE / 8;
    private static final int[] VERTICES = {
            1, 2, 2, 2, 3, 3, 3, 4, 4
    };
    private final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_FLOATS);
    private final float[] array = new float[MAX_FLOATS];
    private int vertices;
    private float u, v;
    private float r, g, b;
    private boolean hasColor = false;
    private boolean hasTexture = false;
    private int len = 3;
    private int pos;
    private int mode;

    private Tesselator() { }

    private Tesselator clear() {
        vertices = 0;
        buffer.clear();
        pos = 0;
        len = 3;
        return this;
    }

    public Tesselator init(int mode) {
        clear().hasColor = false;
        hasTexture = false;
        this.mode = mode;
        return this;
    }

    public Tesselator vertex(float x, float y, float z) {
        if (hasTexture) {
            array[pos++] = u;
            array[pos++] = v;
        }
        if (hasColor) {
            array[pos++] = r;
            array[pos++] = g;
            array[pos++] = b;
        }
        array[pos++] = x;
        array[pos++] = y;
        array[pos++] = z;
        if (((++vertices % VERTICES[mode]) == 0) &&
                (pos >= (MAX_FLOATS - (len * VERTICES[mode])))) {
            draw();
        }
        return this;
    }

    public Tesselator vertex(int x, int y) {
        return vertex(x, y, 0);
    }

    public Tesselator tex(float u, float v) {
        if (!hasTexture) {
            len += 2;
        }
        hasTexture = true;
        this.u = u;
        this.v = v;
        return this;
    }

    public Tesselator color(float r, float g, float b) {
        if (!hasColor) {
            len += 3;
        }
        hasColor = true;
        this.r = r;
        this.g = g;
        this.b = b;
        return this;
    }

    public Tesselator vertexUV(float x, float y, float z, float u, float v) {
        return tex(u, v).vertex(x, y, z);
    }

    public void draw() {
        buffer.clear().put(array, 0, pos).flip();
        if (hasTexture && hasColor) {
            glInterleavedArrays(GL_T2F_C3F_V3F, 0, buffer);
        } else if (hasTexture) {
            glInterleavedArrays(GL_T2F_V3F, 0, buffer);
        } else if (hasColor) {
            glInterleavedArrays(GL_C3F_V3F, 0, buffer);
        } else {
            glInterleavedArrays(GL_V3F, 0, buffer);
        }
        glEnableClientState(GL_VERTEX_ARRAY);
        if (hasTexture) {
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        }
        if (hasColor) {
            glEnableClientState(GL_COLOR_ARRAY);
        }
        glDrawArrays(mode, 0, vertices);
        glDisableClientState(GL_VERTEX_ARRAY);
        if (hasTexture) {
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        }
        if (hasColor) {
            glDisableClientState(GL_COLOR_ARRAY);
        }
        clear();
    }

    public static Tesselator get() {
        return INSTANCE;
    }
}
