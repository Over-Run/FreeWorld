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

package org.overrun.freeworld.block;

import org.overrun.freeworld.client.render.Tesselator;
import org.overrun.freeworld.util.Identifier;
import org.overrun.freeworld.util.Registry;
import org.overrun.freeworld.world.World;
import org.overrun.freeworld.world.phys.AABB;

import java.util.Random;

import static org.overrun.freeworld.block.Blocks.*;

/**
 * @author squid233
 * @since 2021/05/04
 */
public class Block {
    public static final int ATLAS_WIDTH = 128;
    public static final float ATLAS_STRIDE = 16f / 2048f;

    protected boolean shouldRenderFace(World world,
                                     int x,
                                     int y,
                                     int z,
                                     int layer) {
        return !world.isSolidBlock(x, y, z) && (world.isLit(x, y, z) ^ (layer == 1));
    }

    public void randomTick(World world, int x, int y, int z, Random random) { }

    public Tesselator render(Tesselator t, World world, int layer, int x, int y, int z) {
        @SuppressWarnings("PointlessArithmeticExpression")
        float c1 = 255f / 255f;
        float c2 = 204f / 255f;
        float c3 = 153f / 255f;
        if (shouldRenderFace(world, x - 1, y, z, layer)) {
            renderFace(t.color(c1, c1, c1), x, y, z, RIGHT);
        }
        if (shouldRenderFace(world, x + 1, y, z, layer)) {
            renderFace(t.color(c1, c1, c1), x, y, z, LEFT);
        }
        if (shouldRenderFace(world, x, y - 1, z, layer)) {
            renderFace(t.color(c2, c2, c2), x, y, z, BOTTOM);
        }
        if (shouldRenderFace(world, x, y + 1, z, layer)) {
            renderFace(t.color(c2, c2, c2), x, y, z, TOP);
        }
        if (shouldRenderFace(world, x, y, z + 1, layer)) {
            renderFace(t.color(c3, c3, c3), x, y, z, BACK);
        }
        if (shouldRenderFace(world, x, y, z - 1, layer)) {
            renderFace(t.color(c3, c3, c3), x, y, z, FRONT);
        }
        return t;
    }

    public void renderFace(Tesselator t, int x, int y, int z, int face) {
        int tex = getTexture(face);
        float u0 = (float) (tex % ATLAS_WIDTH) / (float) ATLAS_WIDTH;
        float u1 = u0 + ATLAS_STRIDE;
        float v0 = (float) (tex / ATLAS_WIDTH) / (float) ATLAS_WIDTH;
        float v1 = v0 + ATLAS_STRIDE;
        float x1 = x + 1;
        float y1 = y + 1;
        float z1 = z + 1;
        if (face == RIGHT) {
            t.vertexUV(x, y, z, u0, v1)
             .vertexUV(x, y, z1, u1, v1)
             .vertexUV(x, y1, z1, u1, v0)
             .vertexUV(x, y1, z, u0, v0);
        }
        if (face == LEFT) {
            t.vertexUV(x1, y, z1, u0, v1)
             .vertexUV(x1, y, z, u1, v1)
             .vertexUV(x1, y1, z, u1, v0)
             .vertexUV(x1, y1, z1, u0, v0);
        }
        if (face == BOTTOM) {
            t.vertexUV(x, y, z, u0, v1)
             .vertexUV(x1, y, z, u1, v1)
             .vertexUV(x1, y, z1, u1, v0)
             .vertexUV(x, y, z1, u0, v0);
        }
        if (face == TOP) {
            t.vertexUV(x, y1, z1, u0, v1)
             .vertexUV(x1, y1, z1, u1, v1)
             .vertexUV(x1, y1, z, u1, v0)
             .vertexUV(x, y1, z, u0, v0);
        }
        if (face == BACK) {
            t.vertexUV(x1, y, z, u0, v1)
             .vertexUV(x, y, z, u1, v1)
             .vertexUV(x, y1, z, u1, v0)
             .vertexUV(x1, y1, z, u0, v0);
        }
        if (face == FRONT) {
            t.vertexUV(x, y, z1, u0, v1)
             .vertexUV(x1, y, z1, u1, v1)
             .vertexUV(x1, y1, z1, u1, v0)
             .vertexUV(x, y1, z1, u0, v0);
        }
    }

    public Tesselator renderBlankFace(Tesselator t, int x, int y, int z, int face) {
        float x1 = x + 1;
        float y1 = y + 1;
        float z1 = z + 1;
        if (face == RIGHT) {
            t.vertex(x, y, z)
             .vertex(x, y, z1)
             .vertex(x, y1, z1)
             .vertex(x, y1, z);
        }
        if (face == LEFT) {
            t.vertex(x1, y, z1)
             .vertex(x1, y, z)
             .vertex(x1, y1, z)
             .vertex(x1, y1, z1);
        }
        if (face == BOTTOM) {
            t.vertex(x, y, z)
             .vertex(x1, y, z)
             .vertex(x1, y, z1)
             .vertex(x, y, z1);
        }
        if (face == TOP) {
            t.vertex(x, y1, z1)
             .vertex(x1, y1, z1)
             .vertex(x1, y1, z)
             .vertex(x, y1, z);
        }
        if (face == BACK) {
            t.vertex(x1, y, z)
             .vertex(x, y, z)
             .vertex(x, y1, z)
             .vertex(x1, y1, z);
        }
        if (face == FRONT) {
            t.vertex(x, y, z1)
             .vertex(x1, y, z1)
             .vertex(x1, y1, z1)
             .vertex(x, y1, z1);
        }
        return t;
    }

    /**
     * Return that the block blocks lights.
     *
     * @return Whether the block is opacity.
     */
    public boolean isOpacity() {
        return true;
    }

    /**
     * Return that the block can't be instantaneous destruction.
     *
     * @return Whether the block is slowly destruction.
     */
    public boolean isSolid() {
        return true;
    }

    public boolean isAir() {
        return false;
    }

    public AABB getCollision() {
        return getOutline();
    }

    public AABB getOutline() {
        return AABB.of(0, 0, 0, 1, 1, 1);
    }

    public int getTexture(int face) {
        return getRawId();
    }

    public final int getRawId() {
        return Registry.BLOCK.getRawId(this);
    }

    public final Identifier getId() {
        return Registry.BLOCK.get(this);
    }
}
