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

import org.overrun.freeworld.client.util.Frustum;
import org.overrun.freeworld.util.Registry;

/**
 * @author squid233
 * @since 2021/05/04
 */
public class Blocks {
    public static final Block AIR = create("air", new AirBlock());
    public static final Block GRASS_BLOCK = create("grass_block", new Block());
    public static final Block DIRT = create("dirt", new Block());
    public static final Block COBBLESTONE = create("cobblestone", new Block());
    public static final int RIGHT = Frustum.RIGHT;
    public static final int LEFT = Frustum.LEFT;
    public static final int BOTTOM = Frustum.BOTTOM;
    public static final int TOP = Frustum.TOP;
    public static final int BACK = Frustum.BACK;
    public static final int FRONT = Frustum.FRONT;

    static {
        Registry.BLOCK.setDefaultEntry(Blocks.AIR);
    }

    public static <T extends Block> T create(String id, T block) {
        return Registry.BLOCK.register(id, block);
    }

    public static void init() { }
}
