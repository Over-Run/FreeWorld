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

package org.overrun.freeworld.client.util;

import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author squid233
 * @since 2021/05/04
 */
public class GLU extends GL46 {
    public static final float PI = 3.141592653589793238463f;
    private static final FloatBuffer MATRIX = MemoryUtil.memAllocFloat(16);
    private static final float[] IDENTITY_MATRIX = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    public static void gluPerspective(float fovY, float aspect, float zNear, float zFar) {
        float sine, cotangent, deltaZ;
        float radians = fovY / 2 * PI / 180;
        deltaZ = zFar - zNear;
        sine = (float) Math.sin(radians);
        if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
            return;
        }
        cotangent = (float) Math.cos(radians) / sine;
        int oldPos = MATRIX.position();
        glMultMatrixf(MATRIX.put(IDENTITY_MATRIX).position(oldPos)
                .put(0, cotangent / aspect)
                .put(4 + 1, cotangent)
                .put(2 * 4 + 2, - (zFar + zNear) / deltaZ)
                .put(2 * 4 + 3, -1)
                .put(3 * 4 + 2, -2 * zNear * zFar / deltaZ)
                .put(3 * 4 + 3, 0));
    }

    public static void gluPickMatrix(float x, float y, float width, float height, IntBuffer viewport) {
        if (width <= 0 || height <= 0) {
            return;
        }
        /* Translate and scale the picked region to the entire window */
        glTranslatef(
                (viewport.get(viewport.position() + 2) - 2 * (x - viewport.get(viewport.position()))) / width,
                (viewport.get(viewport.position() + 3) - 2 * (y - viewport.get(viewport.position() + 1))) / height,
                0);
        glScalef(viewport.get(viewport.position() + 2) / width,
                viewport.get(viewport.position() + 3) / height,
                1.0f);
    }

    public static void free() {
        MemoryUtil.memFree(MATRIX);
    }
}
