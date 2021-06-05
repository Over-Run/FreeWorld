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

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.overrun.freeworld.world.phys.AABB;

/**
 * @author squid233
 * @since 2021/05/16
 */
public class Frustum extends FrustumIntersection {
    private static final Frustum INSTANCE = new Frustum();
    public static final int RIGHT = PLANE_NX;
    public static final int LEFT = PLANE_PX;
    public static final int BOTTOM = PLANE_NY;
    public static final int TOP = PLANE_PY;
    public static final int BACK = PLANE_NZ;
    public static final int FRONT = PLANE_PZ;

    private Frustum() {
        super(new Matrix4f());
    }

    public static Frustum get() {
        return INSTANCE;
    }

    public boolean isVisible(AABB aabb) {
        return testAab(aabb.x0, aabb.y0, aabb.z0, aabb.x1, aabb.y1, aabb.z1);
    }
}
