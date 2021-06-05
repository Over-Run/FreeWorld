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
import org.overrun.freeworld.util.Registry;
import org.overrun.freeworld.world.phys.AABB;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.overrun.freeworld.FreeWorld.logger;
import static org.overrun.freeworld.block.Blocks.*;

/**
 * @author squid233
 * @since 2021/05/04
 */
public final class World {
    private static final int BLOCK_UPDATE_INTERVAL = 400;
    public final int width;
    public final int height;
    public final int depth;
    private final Block[] blocks;
    private final int[] lightDepths;
    private final List<WorldListener> listeners = new ArrayList<>();
    private final List<AABB> aabbs = new ArrayList<>();
    private final Random random = new Random();
    private int unprocessed;

    public World(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        blocks = new Block[width * height * depth];
        lightDepths = new int[width * depth];
        Arrays.fill(blocks, AIR);
        boolean loaded = load();
        if (!loaded) {
            generateMap();
        }
        calcLightDepths(0, 0, width, depth);
    }

    private void generateMap() {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                for (int y = 0; y < 5; y++) {
                    setBlock(COBBLESTONE, x, y, z);
                    setBlock(DIRT, x, y + 5, z);
                }
                setBlock(GRASS_BLOCK, x, 10, z);
            }
        }
    }

    public void save() {
        try (var fos = new FileOutputStream("world.dat");
             var gos = new GZIPOutputStream(fos);
             var dos = new DataOutputStream(gos)) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < blocks.length; i++) {
                dos.writeInt(blocks[i].getRawId());
            }
        } catch (IOException e) {
            logger.catching(e);
        }
    }

    public boolean load() {
        try (var fis = new FileInputStream("world.dat");
             var gis = new GZIPInputStream(fis);
             var dis = new DataInputStream(gis)) {
            try {
                //noinspection InfiniteLoopStatement
                for (int i = 0, read; ; i++) {
                    read = dis.readInt();
                    blocks[i] = Registry.BLOCK.get(read);
                }
            } catch (EOFException ignored) {
            }
            calcLightDepths(0, 0, width, depth);
            for (WorldListener listener : listeners) {
                listener.allChanged();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void calcLightDepths(int x0, int z0, int x1, int z1) {
        for (int x = x0; x < x1; x++) {
            for (int z = z0; z < z1; z++) {
                int oldDepth = lightDepths[x + z * width];
                int y = depth - 1;
                while (y > 0 && isOpacity(x, y, z)) {
                    --y;
                }
                lightDepths[x + z * width] = y;
                if (oldDepth != y) {
                    int yl0 = Math.min(oldDepth, y);
                    int yl1 = Math.max(oldDepth, y);
                    for (WorldListener listener : listeners) {
                        listener.lightColumnChanged(x, y, yl0, yl1);
                    }
                }
            }
        }
    }

    public void tick() {
        unprocessed += width * height * depth;
        int ticks = unprocessed / BLOCK_UPDATE_INTERVAL;
        unprocessed -= ticks * BLOCK_UPDATE_INTERVAL;
        for (int i = 0; i < ticks; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int z = random.nextInt(depth);
            getBlock(x, y, z).randomTick(this, x, y, z, random);
        }
    }

    public void addListener(WorldListener listener) {
        listeners.add(listener);
    }

    public List<AABB> getCollision(AABB aabb) {
        aabbs.clear();
        int x0 = Math.max((int) aabb.x0, 0);
        int y0 = Math.max((int) aabb.y0, 0);
        int z0 = Math.max((int) aabb.z0, 0);
        int x1 = Math.min((int) (aabb.x1 + 1), width);
        int y1 = Math.min((int) (aabb.y1 + 1), height);
        int z1 = Math.min((int) (aabb.z1 + 1), depth);
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                for (int z = z0; z < z1; z++) {
                    AABB aabb1 = getBlock(x, y, z).getCollision().move(x, y, z);
                    if (aabb1 != AABB.empty()) {
                        aabbs.add(aabb1);
                    }
                }
            }
        }
        return aabbs;
    }

    public boolean isOpacity(int x, int y, int z) {
        return getBlock(x, y, z).isOpacity();
    }

    public boolean isLit(int x, int y, int z) {
        return !inWorld(x, y, z) || y >= lightDepths[x + z * width];
    }

    public boolean isSolidBlock(int x, int y, int z) {
        return getBlock(x, y, z).isSolid();
    }

    public boolean inWorld(int x, int y, int z) {
        return x >= 0 && x < width &&
                y >= 0 && y < height &&
                z >= 0 && z < depth;
    }

    public int getIndex(int x, int y, int z) {
        return (y * depth + z) * width + x;
    }

    public void setBlock(Block block, int x, int y, int z, boolean update) {
        if (inWorld(x, y, z)) {
            blocks[getIndex(x, y, z)] = block == null ? AIR : block;
            calcLightDepths(x, z, 1, 1);
            if (update) {
                for (WorldListener listener : listeners) {
                    listener.blockChanged(x, y, z);
                }
            }
        }
    }

    public void setBlock(Block block, int x, int y, int z) {
        setBlock(block, x, y, z, true);
    }

    public Block getBlock(int x, int y, int z) {
        if (inWorld(x, y, z)) {
            return blocks[getIndex(x, y, z)];
        }
        return AIR;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }
}
