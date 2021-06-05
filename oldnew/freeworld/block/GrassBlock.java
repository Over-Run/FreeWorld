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
import org.overrun.freeworld.world.World;

import java.util.Random;

import static org.overrun.freeworld.block.Blocks.BOTTOM;
import static org.overrun.freeworld.block.Blocks.TOP;

/**
 * @author squid233
 * @since 2021/05/16
 */
public class GrassBlock extends Block {
    @Override
    public void randomTick(World world, int x, int y, int z, Random random) {
        if (!world.isLit(x, y, z)) {
            world.setBlock(Blocks.DIRT, x, y, z);
            return;
        }
        for (int i = 0; i < 4; i++) {
            int xt = x + random.nextInt(3) - 1;
            int yt = y + random.nextInt(5) - 3;
            int zt = z + random.nextInt(3) - 1;
            if (world.getBlock(xt, yt, zt) == Blocks.DIRT && world.isLit(xt, yt, zt)) {
                world.setBlock(this, xt, yt, zt);
            }
        }
    }

    @Override
    public void renderFace(Tesselator t, int x, int y, int z, int face) {
        super.renderFace(t, x, y, z, face);
    }

    @Override
    public int getTexture(int face) {
        int tex = super.getTexture(face);
        if (face == TOP) {
            return tex + ATLAS_WIDTH;
        }
        return face == BOTTOM ? tex + ATLAS_WIDTH * 2 : tex;
    }
}
