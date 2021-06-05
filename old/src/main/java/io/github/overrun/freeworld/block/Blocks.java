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

package io.github.overrun.freeworld.block;

import io.github.overrun.freeworld.client.GlProgram;
import io.github.overrun.freeworld.client.Mesh;
import io.github.overrun.freeworld.client.Texture;

import java.util.Objects;

import static io.github.overrun.freeworld.util.Utils.makeColor1f;

/**
 * @author squid233
 * @since 2021/03/25
 */
public final class Blocks {
    private static final int[] SINGLE_FACE_INDICES = {
            0, 1, 3, 3, 1, 2
    };
    public static final float[] VERTICES_FRONT = {
            // V0
            0, 1, 1,
            // V1
            0, 0, 1,
            // V2
            1, 0, 1,
            // V3
            1, 1, 1
    };
    public static final float[] VERTICES_RIGHT = {
            // V3
            1, 1, 1,
            // V2
            1, 0, 1,
            // V6
            1, 0, 0,
            // V7
            1, 1, 0
    };
    public static final float[] VERTICES_TOP = {
            // V4
            0, 1, 0,
            // V0
            0, 1, 1,
            // V3
            1, 1, 1,
            // V7
            1, 1, 0
    };
    public static final float[] VERTICES_LEFT = {
            // V4
            0, 1, 0,
            // V5
            0, 0, 0,
            // V1
            0, 0, 1,
            // V0
            0, 1, 1
    };
    public static final float[] VERTICES_BACK = {
            // V7
            1, 1, 0,
            // V6
            1, 0, 0,
            // V5
            0, 0, 0,
            // V4
            0, 1, 0
    };
    public static final float[] VERTICES_BOTTOM = {
            // V1
            0, 0, 1,
            // V5
            0, 0, 0,
            // V6
            1, 0, 0,
            // V2
            1, 0, 1
    };
    public static final float[] VERTICES_OVERLAY_FRONT = {
            // V0
            0, 1, 1,
            // V1
            0, 0, 1,
            // V2
            1, 0, 1,
            // V3
            1, 1, 1
    };
    public static final float[] VERTICES_OVERLAY_RIGHT = {
            // V3
            1, 1, 1,
            // V2
            1, 0, 1,
            // V6
            1, 0, 0,
            // V7
            1, 1, 0
    };
    public static final float[] VERTICES_OVERLAY_LEFT = {
            // V4
            0, 1, 0,
            // V5
            0, 0, 0,
            // V1
            0, 0, 1,
            // V0
            0, 1, 1
    };
    public static final float[] VERTICES_OVERLAY_BACK = {
            // V7
            1, 1, 0,
            // V6
            1, 0, 0,
            // V5
            0, 0, 0,
            // V4
            0, 1, 0
    };
    private static final float[] TEX_COORD_TOP = {
            0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f
    };
    private static final float[] TEX_COORD_BOTTOM = {
            0.0f, 0.5f, 0.0f, 1.0f, 0.5f, 1.0f, 0.5f, 0.5f
    };
    private static final float[] TEX_COORD_SIDE = {
            0.5f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 0.0f
    };
    private static final float[] TEX_COORD_OVERLAY_SIDE = {
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f
    };
    public static final int FACE_FRONT = 1;
    public static final int FACE_RIGHT = 2;
    public static final int FACE_TOP = 4;
    public static final int FACE_LEFT = 8;
    public static final int FACE_BACK = 16;
    public static final int FACE_BOTTOM = 32;
    public static final int FACE_OVERLAY_FRONT = 64;
    public static final int FACE_OVERLAY_RIGHT = 128;
    public static final int FACE_OVERLAY_LEFT = 256;
    public static final int FACE_OVERLAY_BACK = 512;

    private static GlProgram program;

    public static Block air;
    public static Block grassBlock;
    public static Block dirt;

    public static Mesh singleFaceMesh(int face,
                                      String blockName,
                                      Texture texture,
                                      float... vertices) {
        return Mesh.of(
                "blockFace_" + blockName + face,
                program,
                vertices,
                makeColor1f(SINGLE_FACE_INDICES.length),
                ((face == FACE_TOP)
                        ? (TEX_COORD_TOP)
                        : ((face == FACE_BOTTOM)
                        ? (TEX_COORD_BOTTOM)
                        : ((face == FACE_FRONT
                        || face == FACE_RIGHT
                        || face == FACE_LEFT
                        || face == FACE_BACK)
                        ? (TEX_COORD_SIDE)
                        : (TEX_COORD_OVERLAY_SIDE)))),
                SINGLE_FACE_INDICES,
                texture
        );
    }

    public static void init(GlProgram glProgram) {
        if (program == null) {
            program = glProgram;
            air = new AirBlock(null);
            grassBlock = create("grass_block");
            var union = Objects.requireNonNull(grassBlock.getUnion()).getMap();
            for (int i = 0; i < SINGLE_FACE_INDICES.length; i++) {
                union.get(FACE_TOP).getColors()[i * 4] = 0.568f;
                union.get(FACE_TOP).getColors()[i * 4 + 1] = 0.741f;
                union.get(FACE_TOP).getColors()[i * 4 + 2] = 0.349f;
                union.get(FACE_OVERLAY_FRONT).getColors()[i * 4] = 0.568f;
                union.get(FACE_OVERLAY_FRONT).getColors()[i * 4 + 1] = 0.741f;
                union.get(FACE_OVERLAY_FRONT).getColors()[i * 4 + 2] = 0.349f;
                union.get(FACE_OVERLAY_RIGHT).getColors()[i * 4] = 0.568f;
                union.get(FACE_OVERLAY_RIGHT).getColors()[i * 4 + 1] = 0.741f;
                union.get(FACE_OVERLAY_RIGHT).getColors()[i * 4 + 2] = 0.349f;
                union.get(FACE_OVERLAY_LEFT).getColors()[i * 4] = 0.568f;
                union.get(FACE_OVERLAY_LEFT).getColors()[i * 4 + 1] = 0.741f;
                union.get(FACE_OVERLAY_LEFT).getColors()[i * 4 + 2] = 0.349f;
                union.get(FACE_OVERLAY_BACK).getColors()[i * 4] = 0.568f;
                union.get(FACE_OVERLAY_BACK).getColors()[i * 4 + 1] = 0.741f;
                union.get(FACE_OVERLAY_BACK).getColors()[i * 4 + 2] = 0.349f;
            }
            dirt = create("dirt");
        }
    }

    private static Block create(String name) {
        return new Block(new BlockMeshUnion(name));
    }
}
