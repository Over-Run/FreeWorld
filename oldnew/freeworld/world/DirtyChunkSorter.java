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

import org.overrun.freeworld.client.util.Frustum;
import org.overrun.freeworld.entity.Player;

import java.util.Comparator;

/**
 * @author squid233
 * @since 2021/05/16
 */
public class DirtyChunkSorter implements Comparator<Chunk> {
    private final Player player;
    private final long now = System.currentTimeMillis();

    public DirtyChunkSorter(Player player) {
        this.player = player;
    }

    @Override
    public int compare(Chunk c0, Chunk c1) {
        Frustum frustum = Frustum.get();
        boolean i0 = frustum.isVisible(c0.aabb);
        boolean i1 = frustum.isVisible(c1.aabb);
        if (i0 && !i1) {
            return -1;
        } else if (i1 && !i0) {
            return 1;
        } else {
            int t0 = (int) ((now - c0.dirtiedTime) / 2000);
            int t1 = (int) ((now - c1.dirtiedTime) / 2000);
            if (t0 < t1) {
                return -1;
            } else if (t0 > t1) {
                return 1;
            } else {
                return Float.compare(c0.distanceToSqr(player), c1.distanceToSqr(player));
            }
        }
    }
}
