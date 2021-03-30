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
            // front
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
            // right
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
            // top
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
            // left
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
            // back
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
            // bottom
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
            // overlay front
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
            // overlay right
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
            // overlay left
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
            // overlay back
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
    /**public static Mesh faceFront;
    public static Mesh faceRight;
    public static Mesh faceTop;
    public static Mesh faceLeft;
    public static Mesh faceBack;
    public static Mesh faceBottom;
    public static Mesh faceOverlayFront;
    public static Mesh faceOverlayRight;
    public static Mesh faceOverlayLeft;
    public static Mesh faceOverlayBack;*/

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

    /*private static void initFaceMesh() {
        faceFront = singleFaceMesh(
                FACE_FRONT,
                // V0
                0, 1, 1,
                // V1
                0, 0, 1,
                // V2
                1, 0, 1,
                // V3
                1, 1, 1
        );
        faceRight = singleFaceMesh(
                FACE_RIGHT,
                // V3
                1, 1, 1,
                // V2
                1, 0, 1,
                // V6
                1, 0, 0,
                // V7
                1, 1, 0
        );
        faceTop = singleFaceMesh(
                FACE_TOP,
                // V4
                0, 1, 0,
                // V0
                0, 1, 1,
                // V3
                1, 1, 1,
                // V7
                1, 1, 0
        );
        faceLeft = singleFaceMesh(
                FACE_LEFT,
                // V4
                0, 1, 0,
                // V5
                0, 0, 0,
                // V1
                0, 0, 1,
                // V0
                0, 1, 1
        );
        faceBack = singleFaceMesh(
                FACE_BACK,
                // V7
                1, 1, 0,
                // V6
                1, 0, 0,
                // V5
                0, 0, 0,
                // V4
                0, 1, 0
        );
        faceBottom = singleFaceMesh(
                FACE_BOTTOM,
                // V1
                0, 0, 1,
                // V5
                0, 0, 0,
                // V6
                1, 0, 0,
                // V2
                1, 0, 1
        );
    }*/

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
            /*Mesh mesh = grassBlock.getMesh();
            if (mesh != null) {
                for (int i = 8; i < 40; i++) {
                    if (i == 12) {
                        i = 24;
                    }
                    mesh.getColors()[i * 4] = 0.568f;
                    mesh.getColors()[i * 4 + 1] = 0.741f;
                    mesh.getColors()[i * 4 + 2] = 0.349f;
                }
            }*/
            dirt = create("dirt");
            //initFaceMesh();
        }
    }

    /*private static final float[] VERTICES = {
            // front
            // V0
            0, 1, 1,
            // V1
            0, 0, 1,
            // V2
            1, 0, 1,
            // V3
            1, 1, 1,
            // right
            // V3
            1, 1, 1,
            // V2
            1, 0, 1,
            // V6
            1, 0, 0,
            // V7
            1, 1, 0,
            // top
            // V4
            0, 1, 0,
            // V0
            0, 1, 1,
            // V3
            1, 1, 1,
            // V7
            1, 1, 0,
            // left
            // V4
            0, 1, 0,
            // V5
            0, 0, 0,
            // V1
            0, 0, 1,
            // V0
            0, 1, 1,
            // back
            // V7
            1, 1, 0,
            // V6
            1, 0, 0,
            // V5
            0, 0, 0,
            // V4
            0, 1, 0,
            // bottom
            // V1
            0, 0, 1,
            // V5
            0, 0, 0,
            // V6
            1, 0, 0,
            // V2
            1, 0, 1,
            // overlay front
            // V0
            0, 1, 1,
            // V1
            0, 0, 1,
            // V2
            1, 0, 1,
            // V3
            1, 1, 1,
            // overlay right
            // V3
            1, 1, 1,
            // V2
            1, 0, 1,
            // V6
            1, 0, 0,
            // V7
            1, 1, 0,
            // overlay left
            // V4
            0, 1, 0,
            // V5
            0, 0, 0,
            // V1
            0, 0, 1,
            // V0
            0, 1, 1,
            // overlay back
            // V7
            1, 1, 0,
            // V6
            1, 0, 0,
            // V5
            0, 0, 0,
            // V4
            0, 1, 0
    };
    private static final float[] TEX_COORDS = {
            // front
            0.5f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 0.0f,
            // right
            0.5f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 0.0f,
            // top
            0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f,
            // left
            0.5f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 0.0f,
            // back
            0.5f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 0.0f,
            // bottom
            0.0f, 0.5f, 0.0f, 1.0f, 0.5f, 1.0f, 0.5f, 0.5f,
            // overlay front
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f,
            // overlay right
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f,
            // overlay left
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f,
            // overlay back
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f
    };
    private static final int[] INDICES = {
            // front
            0, 1, 3, 3, 1, 2,
            // right
            4, 5, 7, 7, 5, 6,
            // top
            8, 9, 11, 11, 9, 10,
            // left
            12, 13, 15, 15, 13, 14,
            // back
            16, 17, 19, 19, 17, 18,
            // bottom
            20, 21, 23, 23, 21, 22,
            // overlay front
            24, 25, 27, 27, 25, 26,
            // overlay right
            28, 29, 31, 31, 29, 30,
            // overlay left
            32, 33, 35, 35, 33, 34,
            // overlay back
            36, 37, 39, 39, 37, 38
    };*/

    private static Block create(String name) {
        return new Block(new BlockMeshUnion(name));
    }
}
