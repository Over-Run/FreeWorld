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

package io.github.overrun.freeworld.world

import io.github.overrun.freeworld.block.Block
import io.github.overrun.freeworld.block.Blocks
import io.github.overrun.freeworld.block.Blocks.air
import io.github.overrun.freeworld.client.GlProgram
import io.github.overrun.freeworld.client.Mesh
import io.github.overrun.freeworld.client.Transformation
import io.github.overrun.freeworld.client.Window
import org.lwjgl.opengl.GL11.*

/**
 * @author squid233
 * @since 2021/03/24
 */
class World(
    val width: Int,
    val height: Int,
    val depth: Int
) {
    private lateinit var box: Mesh
    private val blocks = Array(width * height * depth) { air }
//    private val blockPicker = BlockPicker()

    init {
        for (x in 0 until width) {
            for (z in 0 until depth) {
                setBlock(x, 0, z, Blocks.dirt)
                setBlock(x, 1, z, Blocks.grassBlock)
            }
        }
    }

    fun render(program: GlProgram, transformation: Transformation) {
        val viewMatrix = transformation.getViewMatrix()
        var i = 0
        for (x in 0 until width) {
            for (z in 0 until depth) {
                for (y in 0 until height) {
                    val block = getBlock(x, y, z)
                    block.x = x
                    block.y = y
                    block.z = z
                    if (block != air && (
                                getBlock(x - 1, y, z) == air
                                        || getBlock(x + 1, y, z) == air
                                        || getBlock(x, y - 1, z) == air
                                        || getBlock(x, y + 1, z) == air
                                        || getBlock(x, y, z - 1) == air
                                        || getBlock(x, y, z + 1) == air
                                )
                    ) {
                        program.setUniform(
                            "modelViewMatrix",
                            transformation.getModelViewMatrix(block, viewMatrix)
                        )
                        if (blockPicker.targetIndex == i) {
                            if (!this::box.isInitialized)
                                box = Mesh.of(
                                    "box",
                                    program,
                                    floatArrayOf(
                                        0f, 0f, 0f,
                                        0f, 1f, 0f,
                                        1f, 1f, 0f,
                                        1f, 0f, 0f,
                                        0f, 0f, -1f,
                                        0f, 1f, -1f,
                                        1f, 1f, -1f,
                                        1f, 0f, -1f
                                    ),
                                    floatArrayOf(
                                        0f, 0f, 0f, 1f,
                                        0f, 0f, 0f, 1f,
                                        0f, 0f, 0f, 1f,
                                        0f, 0f, 0f, 1f,
                                        0f, 0f, 0f, 1f,
                                        0f, 0f, 0f, 1f,
                                        0f, 0f, 0f, 1f,
                                        0f, 0f, 0f, 1f
                                    ),
                                    null,
                                    intArrayOf(
                                        // front
                                        3, 2, 1, 0,
                                        // right
                                        7, 6, 2, 3,
                                        // top
                                        1, 2, 6, 5,
                                        // left
                                        0, 1, 5, 4,
                                        // back
                                        4, 5, 6, 7,
                                        // bottom
                                        0, 4, 7, 3
                                    ),
                                    mode = GL_QUADS
                                )
                            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
                            box.render()
                            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
                        }
                        block.render()
                    }
                    ++i
                }
            }
        }
    }

    fun pick(transformation: Transformation, window: Window) {
//        blockPicker.pick(transformation, blocks)
        transformation.getPickMatrix(window)
        for (block in blocks) {
        }
    }

    fun inBound(x: Int, y: Int, z: Int) =
        x in 0 until width
                && y in 0 until height
                && z in 0 until depth

    fun getIndex(x: Int, y: Int, z: Int) =
        (x % width) + (y * width) + (z * width * height)

    fun getBlock(x: Int, y: Int, z: Int) =
        if (inBound(x, y, z)) blocks[getIndex(x, y, z)]
        else air

    fun setBlock(x: Int, y: Int, z: Int, block: Block) {
        if (inBound(x, y, z))
            blocks[getIndex(x, y, z)] = block
    }
}