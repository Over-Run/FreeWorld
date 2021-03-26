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
import io.github.overrun.freeworld.client.Transformation
import org.joml.Matrix4f
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
    private val blocks = Array(width * height * depth) { air }

    init {
        for (x in 0 until width) {
            for (z in 0 until depth) {
                for (y in 0 until 4) {
                    setBlock(x, y, z, Blocks.dirt)
                }
                setBlock(x, 4, z, Blocks.grassBlock)
            }
        }
    }

    fun render(program: GlProgram, transformation: Transformation, viewMatrix: Matrix4f) {
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
                        block.render()
                    }
                    ++i
                }
            }
        }
    }

    fun pick(program: GlProgram, transformation: Transformation, viewMatrix: Matrix4f) {
        glInitNames()
        glPushName(0)
        glPushName(0)
        var i = 0
        for (x in 0 until width) {
            glLoadName(x)
            glPushName(0)
            for (y in 0 until height) {
                glLoadName(y)
                glPushName(0)
                for (z in 0 until depth) {
                    glLoadName(z)
                    glPushName(0)
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
                        for (j in 0 until 6) {
                            glLoadName(j)
                            block.render(j)
                        }
                    }
                    glPopName()
                    ++i
                }
                glPopName()
            }
            glPopName()
        }
        glPopName()
        glPopName()
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