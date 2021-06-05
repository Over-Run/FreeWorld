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

package org.overrun.freeworld.world.phys;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author squid233
 * @since 2021/05/05
 */
public class AABB {
    private static final Map<String, AABB> FACTORY = new HashMap<>();
    private final float epsilon = 0;
    public float x0;
    public float y0;
    public float z0;
    public float x1;
    public float y1;
    public float z1;

    public static final class Empty extends AABB {
        public static final Empty EMPTY = new Empty();

        private Empty() {
            super(0, 0, 0, 0, 0, 0);
        }

        @Override
        public Empty expand(float xa, float ya, float za) {
            return this;
        }

        @Override
        public Empty grow(float xa, float ya, float za) {
            return this;
        }

        @Override
        public Empty move(float xa, float ya, float za) {
            return this;
        }
    }

    public static Empty empty() {
        return Empty.EMPTY;
    }

    private AABB(float x0, float y0, float z0, float x1, float y1, float z1) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }

    public static AABB of(float x0,
                          float y0,
                          float z0,
                          float x1,
                          float y1,
                          float z1) {
        return FACTORY.computeIfAbsent(toShortString(x0, y0, z0, x1, y1, z1),
                s -> new AABB(x0, y0, z0, x1, y1, z1));
    }

    public AABB expand(float xa, float ya, float za) {
        return of(xa < 0 ? x0 + xa : x0,
                ya < 0 ? y0 + ya : y0,
                za < 0 ? z0 + za : z0,
                xa > 0 ? x1 + xa : x1,
                ya > 0 ? y1 + ya : y1,
                za > 0 ? z1 + za : z1);
    }

    public AABB grow(float xa, float ya, float za) {
        return of(x0 - xa,
                y0 - ya,
                z0 - za,
                x1 + xa,
                y1 + ya,
                z1 + za);
    }

    public float clipXCollide(AABB aabb, float xa) {
        if (aabb.y1 > y0 && aabb.y0 < y1 &&
                aabb.z1 > z0 && aabb.z0 < z1) {
            float max;
            if (xa > 0 && aabb.x1 <= x0) {
                max = x0 - aabb.x1 - epsilon;
                if (max < xa) {
                    xa = max;
                }
            }
            if (xa < 0 && aabb.x0 >= x1) {
                max = x1 - aabb.x0 + epsilon;
                if (max > xa) {
                    xa = max;
                }
            }
        }
        return xa;
    }

    public float clipYCollide(AABB aabb, float ya) {
        if (aabb.x1 > x0 && aabb.x0 < x1 &&
                aabb.z1 > z0 && aabb.z0 < z1) {
            float max;
            if (ya > 0 && aabb.y1 <= y0) {
                max = y0 - aabb.y1 - epsilon;
                if (max < ya) {
                    ya = max;
                }
            }
            if (ya < 0 && aabb.y0 >= y1) {
                max = y1 - aabb.y0 + epsilon;
                if (max > ya) {
                    ya = max;
                }
            }
        }
        return ya;
    }

    public float clipZCollide(AABB aabb, float za) {
        if (aabb.x1 > x0 && aabb.x0 < x1 &&
                aabb.y1 > y0 && aabb.y0 < y1) {
            float max;
            if (za > 0 && aabb.z1 <= z0) {
                max = z0 - aabb.z1 - epsilon;
                if (max < za) {
                    za = max;
                }
            }
            if (za < 0 && aabb.z0 >= z1) {
                max = z1 - aabb.z0 + epsilon;
                if (max > za) {
                    za = max;
                }
            }
        }
        return za;
    }

    public boolean intersects(AABB aabb) {
        return aabb.x1 > x0 && aabb.x0 < x1 &&
                aabb.y1 > y0 && aabb.y0 < y1 &&
                aabb.z1 > z0 && aabb.z0 < z1;
    }

    public AABB move(float xa, float ya, float za) {
        return of(x0 + xa, y0 + ya, z0 + za, x1 + xa, y1 + ya, z1 + za);
    }

    public static String toShortString(float x0,
                                       float y0,
                                       float z0,
                                       float x1,
                                       float y1,
                                       float z1) {
        return x0 + ", " + y0 + ", " + z0 + ", " + x1 + ", " + y1 + ", " + z1;
    }

    public String toShortString() {
        return toShortString(x0, y0, z0, x1, y1, z1);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AABB.class.getSimpleName() + "[", "]")
                .add("x0=" + x0)
                .add("y0=" + y0)
                .add("z0=" + z0)
                .add("x1=" + x1)
                .add("y1=" + y1)
                .add("z1=" + z1)
                .toString();
    }
}
