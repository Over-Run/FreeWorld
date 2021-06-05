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

package org.overrun.freeworld.client.world;

import org.overrun.freeworld.block.Block;
import org.overrun.freeworld.client.Texture;
import org.overrun.freeworld.client.render.Tesselator;
import org.overrun.freeworld.client.util.Frustum;
import org.overrun.freeworld.entity.Player;
import org.overrun.freeworld.util.HitResult;
import org.overrun.freeworld.util.Identifier;
import org.overrun.freeworld.world.Chunk;
import org.overrun.freeworld.world.DirtyChunkSorter;
import org.overrun.freeworld.world.World;
import org.overrun.freeworld.world.WorldListener;
import org.overrun.freeworld.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 2021/05/05
 */
public class WorldRenderer implements WorldListener {
    public static final int MAX_REBUILDS_PER_FRAME = 8;
    public static final int CHUNK_SIZE = 16;
    private final World world;
    private final Chunk[] chunks;
    private final int xChunks;
    private final int yChunks;
    private final int zChunks;

    public WorldRenderer(World world) {
        this.world = world;
        world.addListener(this);
        xChunks = world.width / CHUNK_SIZE;
        yChunks = world.height / CHUNK_SIZE;
        zChunks = world.depth / CHUNK_SIZE;
        chunks = new Chunk[xChunks * yChunks * zChunks];
        for (int y = 0; y < yChunks; y++) {
            for (int x = 0; x < xChunks; x++) {
                for (int z = 0; z < zChunks; z++) {
                    int x0 = x * CHUNK_SIZE;
                    int y0 = y * CHUNK_SIZE;
                    int z0 = z * CHUNK_SIZE;
                    int x1 = (x + 1) * CHUNK_SIZE;
                    int y1 = (y + 1) * CHUNK_SIZE;
                    int z1 = (z + 1) * CHUNK_SIZE;
                    if (x1 > world.width) {
                        x1 = world.width;
                    }
                    if (y1 > world.depth) {
                        y1 = world.depth;
                    }
                    if (z1 > world.height) {
                        z1 = world.height;
                    }
                    chunks[(x + y * xChunks) * zChunks + z] =
                            new Chunk(world,
                                    x0,
                                    y0,
                                    z0,
                                    x1,
                                    y1,
                                    z1);
                }
            }
        }
    }

    public Optional<List<Chunk>> getAllDirtyChunks() {
        List<Chunk> list = null;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < chunks.length; i++) {
            Chunk chunk = chunks[i];
            if (chunk.isDirty()) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(chunk);
            }
        }
        return Optional.ofNullable(list);
    }

    public void updateDirtyChunks(Player player) {
        getAllDirtyChunks().ifPresent(dirty -> {
            dirty.sort(new DirtyChunkSorter(player));
            for (int i = 0;
                 i < MAX_REBUILDS_PER_FRAME && i < dirty.size();
                 i++) {
                dirty.get(i).rebuild();
            }
        });
    }

    public void render(int layer) {
        glEnable(GL_TEXTURE_2D);
        Texture.bind(new Identifier("textures/block/block_atlas.png"));
        Frustum frustum = Frustum.get();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < chunks.length; i++) {
            Chunk chunk = chunks[i];
            if (frustum.isVisible(chunk.aabb)) {
                chunk.render(layer);
            }
        }
        glDisable(GL_TEXTURE_2D);
    }

    public void pick(Player player) {
        Tesselator t = Tesselator.get();
        Frustum frustum = Frustum.get();
        float r = 5;
        AABB box = player.aabb.grow(r, r, r);
        int x0 = (int) box.x0;
        int x1 = (int) (box.x1 + 1);
        int y0 = (int) box.y0;
        int y1 = (int) (box.y1 + 1);
        int z0 = (int) box.z0;
        int z1 = (int) (box.z1 + 1);
        glInitNames();
        glPushName(0);
        glPushName(0);
        for (int x = x0; x < x1; x++) {
            glLoadName(x);
            glPushName(0);
            for (int y = y0; y < y1; y++) {
                glLoadName(y);
                glPushName(0);
                for (int z = z0; z < z1; z++) {
                    Block block = world.getBlock(x, y, z);
                    if (!block.isAir()
                            && frustum.isVisible(block.getOutline())) {
                        glLoadName(z);
                        glPushName(0);
                        for (int i = 0; i < 6; i++) {
                            glLoadName(i);
                            block.renderBlankFace(t.init(GL_QUADS), x, y, z, i)
                                    .draw();
                        }
                        glPopName();
                    }
                }
                glPopName();
            }
            glPopName();
        }
        glPopName();
        glPopName();
    }

    public void renderHit(HitResult hit) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glColor4f(0, 0, 0, 0.6f);
        float x0 = hit.x - 0.001f;
        float y0 = hit.y - 0.001f;
        float z0 = hit.z - 0.001f;
        float x1 = x0 + 0.002f;
        float y1 = y0 + 0.002f;
        float z1 = z0 + 0.002f;
        Tesselator.get().init(GL_QUADS)
         .vertex(x0, y0, z1)
         .vertex(x0, y0, z0)
         .vertex(x0, y1, z0)
         .vertex(x0, y1, z1)
         .vertex(x1, y0, z0)
         .vertex(x1, y0, z1)
         .vertex(x1, y1, z1)
         .vertex(x1, y1, z0)
         .vertex(x0, y0, z1)
         .vertex(x1, y0, z1)
         .vertex(x1, y0, z0)
         .vertex(x0, y0, z0)
         .vertex(x0, y1, z0)
         .vertex(x1, y1, z0)
         .vertex(x1, y1, z1)
         .vertex(x0, y1, z1)
         .vertex(x1, y0, z1)
         .vertex(x0, y0, z1)
         .vertex(x0, y1, z1)
         .vertex(x1, y1, z1)
         .vertex(x0, y0, z0)
         .vertex(x1, y0, z0)
         .vertex(x1, y1, z0)
         .vertex(x0, y1, z0)
         .draw();
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glDisable(GL_BLEND);
    }

    public void markDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
        x0 /= CHUNK_SIZE;
        x1 /= CHUNK_SIZE;
        y0 /= CHUNK_SIZE;
        y1 /= CHUNK_SIZE;
        z0 /= CHUNK_SIZE;
        z1 /= CHUNK_SIZE;
        if (x0 < 0) {
            x0 = 0;
        }
        if (y0 < 0) {
            y0 = 0;
        }
        if (z0 < 0) {
            z0 = 0;
        }
        if (x1 >= xChunks) {
            x1 = xChunks - 1;
        }
        if (y1 >= yChunks) {
            y1 = yChunks - 1;
        }
        if (z1 >= zChunks) {
            z1 = zChunks - 1;
        }
        for (int x = x0; x <= x1; x++) {
            for (int y = y0; y <= y1; y++) {
                for (int z = z0; z <= z1; z++) {
                    chunks[(x + y * xChunks) * zChunks + z].markDirty();
                }
            }
        }
    }

    @Override
    public void blockChanged(int x, int y, int z) {
        markDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    @Override
    public void lightColumnChanged(int x, int z, int y0, int y1) {
        markDirty(x - 1, y0 - 1, z - 1, x + 1, y1 + 1, z + 1);
    }

    @Override
    public void allChanged() {
        markDirty(0, 0, 0, world.width, world.height, world.depth);
    }
}
