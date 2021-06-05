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

package org.overrun.freeworld.world;

import org.overrun.freeworld.block.Block;
import org.overrun.freeworld.client.render.Tesselator;
import org.overrun.freeworld.entity.Player;
import org.overrun.freeworld.world.phys.AABB;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 2021/05/05
 */
public class Chunk {
    private static final Tesselator TES = Tesselator.get();
    public static int updates;
    private static long totalTime;
    private static int totalUpdates;
    public AABB aabb;
    public final World world;
    public final int x0;
    public final int y0;
    public final int z0;
    public final int x1;
    public final int y1;
    public final int z1;
    public final float x;
    public final float y;
    public final float z;
    private boolean dirty = true;
    private final int lists;
    public long dirtiedTime = 0L;

    public Chunk(World world, int x0, int y0, int z0, int x1, int y1, int z1) {
        this.world = world;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        x = (float) (x0 + x1) / 2;
        y = (float) (y0 + y1) / 2;
        z = (float) (z0 + z1) / 2;
        aabb = AABB.of(x0, y0, z0, x1, y1, z1);
        lists = glGenLists(2);
    }

    private void rebuild(int layer) {
        dirty = false;
        ++updates;
        long before = System.nanoTime();
        glNewList(lists + layer, GL_COMPILE);
        TES.init(GL_QUADS);
        int renderedBlocks = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    Block block = world.getBlock(x, y, z);
                    if (!block.isAir()) {
                        block.render(TES, world, layer, x, y, z);
                        ++renderedBlocks;
                    }
                }
            }
        }
        TES.draw();
        glEndList();
        long after = System.nanoTime();
        if (renderedBlocks > 0) {
            totalTime += after - before;
            ++totalUpdates;
        }
    }

    public void rebuild() {
        rebuild(0);
        rebuild(1);
    }

    public void render(int layer) {
        glCallList(lists + layer);
    }

    public void markDirty() {
        if (!dirty) {
            dirtiedTime = System.currentTimeMillis();
        }
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public float distanceToSqr(Player player) {
        float xd = player.x - x;
        float yd = player.y - y;
        float zd = player.z - z;
        return xd * xd + yd * yd + zd * zd;
    }
}
