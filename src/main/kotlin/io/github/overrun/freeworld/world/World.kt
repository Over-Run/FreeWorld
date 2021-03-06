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
import io.github.overrun.freeworld.block.Blocks.*
import io.github.overrun.freeworld.client.GlProgram
import io.github.overrun.freeworld.client.Transformation
import io.github.overrun.freeworld.entity.player.Player
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*

/**
 * This is [world][World].
 * This is not [free][io.github.overrun.freeworld.FreeWorld].
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
        val layer = 2
        for (x in 0 until width) {
            for (z in 0 until depth) {
                for (y in 0 until layer) {
                    setBlock(x, y, z, dirt)
                }
                setBlock(x, layer, z, grassBlock)
            }
        }
    }

    fun render(program: GlProgram, viewMatrix: Matrix4f) {
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
                            Transformation.getModelViewMatrix(block, viewMatrix)
                        )
                        var result = 0
                        if (getBlock(x - 1, y, z) == air)
                            result += FACE_LEFT + FACE_OVERLAY_LEFT
                        if (getBlock(x + 1, y, z) == air)
                            result += FACE_RIGHT + FACE_OVERLAY_RIGHT
                        if (getBlock(x, y - 1, z) == air)
                            result += FACE_BOTTOM
                        if (getBlock(x, y + 1, z) == air)
                            result += FACE_TOP
                        if (getBlock(x, y, z - 1) == air)
                            result += FACE_BACK + FACE_OVERLAY_BACK
                        if (getBlock(x, y, z + 1) == air)
                            result += FACE_FRONT + FACE_OVERLAY_FRONT
                        block.render(result)
                    }
                }
            }
        }
    }

    fun pick(program: GlProgram, viewMatrix: Matrix4f) {
        glInitNames()
        glPushName(0)
        glPushName(0)
        val boxRadius = 6
        val pX0 = (Player.x - boxRadius).toInt()
        val pY0 = (Player.y - boxRadius).toInt()
        val pZ0 = (Player.z - boxRadius).toInt()
        val pX1 = (Player.x + boxRadius + 1).toInt()
        val pY1 = (Player.y + boxRadius + 1).toInt()
        val pZ1 = (Player.z + boxRadius + 1).toInt()
        for (x in pX0 until pX1) {
            glLoadName(x)
            glPushName(0)
            for (y in pY0 until pY1) {
                glLoadName(y)
                glPushName(0)
                for (z in pZ0 until pZ1) {
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
                            Transformation.getModelViewMatrix(block, viewMatrix)
                        )
                        glColor3f(1f, 1f, 1f)
                        glLoadName(FACE_FRONT)
                        if (getBlock(x, y, z + 1) == air)
                            for (set in block.getCollisionShape().sets) {
                                glBegin(GL_QUADS)
                                // V0
                                glVertex3f(set.originX, set.endY, set.endZ)
                                // V1
                                glVertex3f(set.originX, set.originY, set.endZ)
                                // V2
                                glVertex3f(set.endX, set.originY, set.endZ)
                                // V3
                                glVertex3f(set.endX, set.endY, set.endZ)
                                glEnd()
                            }
                        glLoadName(FACE_RIGHT)
                        if (getBlock(x + 1, y, z) == air)
                            for (set in block.getCollisionShape().sets) {
                                glBegin(GL_QUADS)
                                // V3
                                glVertex3f(set.endX, set.endY, set.endZ)
                                // V2
                                glVertex3f(set.endX, set.originY, set.endZ)
                                // V6
                                glVertex3f(set.endX, set.originY, set.originZ)
                                // V7
                                glVertex3f(set.endX, set.endY, set.originZ)
                                glEnd()
                            }
                        glLoadName(FACE_TOP)
                        if (getBlock(x, y + 1, z) == air)
                            for (set in block.getCollisionShape().sets) {
                                glBegin(GL_QUADS)
                                // V4
                                glVertex3f(set.originX, set.endY, set.originZ)
                                // V0
                                glVertex3f(set.originX, set.endY, set.endZ)
                                // V3
                                glVertex3f(set.endX, set.endY, set.endZ)
                                // V7
                                glVertex3f(set.endX, set.endY, set.originZ)
                                glEnd()
                            }
                        glLoadName(FACE_LEFT)
                        if (getBlock(x - 1, y, z) == air)
                            for (set in block.getCollisionShape().sets) {
                                glBegin(GL_QUADS)
                                // V4
                                glVertex3f(set.originX, set.endY, set.originZ)
                                // V5
                                glVertex3f(set.originX, set.originY, set.originZ)
                                // V1
                                glVertex3f(set.originX, set.originY, set.endZ)
                                // V0
                                glVertex3f(set.originX, set.endY, set.endZ)
                                glEnd()
                            }
                        glLoadName(FACE_BACK)
                        if (getBlock(x, y, z - 1) == air)
                            for (set in block.getCollisionShape().sets) {
                                glBegin(GL_QUADS)
                                // V7
                                glVertex3f(set.endX, set.endY, set.originZ)
                                // V6
                                glVertex3f(set.endX, set.originY, set.originZ)
                                // V5
                                glVertex3f(set.originX, set.originY, set.originZ)
                                // V4
                                glVertex3f(set.originX, set.endY, set.originZ)
                                glEnd()
                            }
                        glLoadName(FACE_BOTTOM)
                        if (getBlock(x, y - 1, z) == air)
                            for (set in block.getCollisionShape().sets) {
                                glBegin(GL_QUADS)
                                // V1
                                glVertex3f(set.originX, set.originY, set.endZ)
                                // V5
                                glVertex3f(set.originX, set.originY, set.originZ)
                                // V6
                                glVertex3f(set.endX, set.originY, set.originZ)
                                // V2
                                glVertex3f(set.endX, set.originY, set.endZ)
                                glEnd()
                            }
                    }
                    glPopName()
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
        //(x % width) + (y * width) + (z * width * height)
        (x) + (y * width * depth) + (z * width)

    fun getBlock(x: Int, y: Int, z: Int) =
        if (inBound(x, y, z)) blocks[getIndex(x, y, z)]
        else air

    @JvmOverloads
    fun setBlock(x: Int, y: Int, z: Int, block: Block, force: Boolean = false) {
        if (!inBound(x, y, z)) return
        if (!force && getBlock(x, y, z) != air) return
        blocks[getIndex(x, y, z)] = block
    }
}