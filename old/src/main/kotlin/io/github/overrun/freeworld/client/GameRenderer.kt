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

package io.github.overrun.freeworld.client

import io.github.overrun.freeworld.block.Block
import io.github.overrun.freeworld.block.Blocks
import io.github.overrun.freeworld.client.GlStateManager.disableCullFace
import io.github.overrun.freeworld.client.GlStateManager.enableCullFace
import io.github.overrun.freeworld.client.game.BaseGameObject2D
import io.github.overrun.freeworld.entity.player.Player
import io.github.overrun.freeworld.util.HitResult
import io.github.overrun.freeworld.util.Utils.intArrayOfSize
import io.github.overrun.freeworld.util.Utils.makeColor1f
import io.github.overrun.freeworld.world.World
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.system.MemoryUtil
import java.io.Closeable

/**
 * @author squid233
 * @since 2021/03/18
 */
class GameRenderer : Closeable {
    private val selectBuffer = MemoryUtil.memAllocInt(2000)
    private val hitResult = HitResult()
    private lateinit var block: Block
    private lateinit var world: World
    private lateinit var program: GlProgram
    private lateinit var program2D: GlProgram
    private lateinit var crossHair: BaseGameObject2D
    private lateinit var blocksTab: BaseGameObject2D
    private lateinit var hitResultBox: Mesh

    fun init() {
        program = GlProgram.of("shader/core/block")
        program2D = GlProgram.of("shader/core/gui")
        Blocks.init(program)
        block = Blocks.grassBlock
        FreeWorldClient.world = World(64, 64, 64)
        world = FreeWorldClient.world!!
        crossHair = BaseGameObject2D(
            Mesh.of(
                "cross_hair",
                program2D,
                floatArrayOf(
                    -9f, 1f, 0f,
                    -9f, -1f, 0f,
                    9f, -1f, 0f,
                    9f, 1f, 0f,
                    -1f, -9f, 0f,
                    -1f, 9f, 0f,
                    1f, 9f, 0f,
                    1f, -9f, 0f
                ),
                makeColor1f(8),
                null,
                intArrayOf(
                    0, 1, 2, 3, 4, 5, 6, 7
                ),
                mode = GL_QUADS
            )
        )
        blocksTab = BaseGameObject2D(
            Mesh.of(
                "blocksTab",
                program2D,
                floatArrayOf(
                    -250f, -150f, 0f,
                    -250f, 150f, 0f,
                    250f, 150f, 0f,
                    250f, -150f, 0f
                ),
                floatArrayOf(
                    0f, 0f, 0f, 0.5f,
                    0f, 0f, 0f, 0.5f,
                    0f, 0f, 0f, 0.5f,
                    0f, 0f, 0f, 0.5f
                ),
                null,
                intArrayOf(0, 1, 2, 3),
                mode = GL_QUADS
            )
        )
        hitResultBox = Mesh.of(
            "hitResultBox",
            program,
            floatArrayOf(
                -0.001f, -0.001f, -0.001f,
                -0.001f, 1.001f, -0.001f,
                1.001f, 1.001f, -0.001f,
                1.001f, -0.001f, -0.001f,
                -0.001f, -0.001f, 1.001f,
                -0.001f, 1.001f, 1.001f,
                1.001f, 1.001f, 1.001f,
                1.001f, -0.001f, 1.001f
                /*0f, 0f, 1f,
                  0f, 1f, 1f,
                  1f, 1f, 1f,
                  1f, 0f, 1f,
                  0f, 0f, 0f,
                  0f, 1f, 0f,
                  1f, 0f, 0f,
                  1f, 0f, 0f*/
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
    }

    fun input(window: Window) {
        if (window.isKeyPressed(GLFW_KEY_1))
            block = Blocks.grassBlock
        if (window.isKeyPressed(GLFW_KEY_2))
            block = Blocks.dirt
        if (Player.playing && !hitResult.isNull) {
            if (window.isMousePressed(GLFW_MOUSE_BUTTON_LEFT))
                world.setBlock(hitResult.x, hitResult.y, hitResult.z, Blocks.air, true)
            if (window.isMousePressed(GLFW_MOUSE_BUTTON_RIGHT)) {
                when (hitResult.face) {
                    Blocks.FACE_FRONT ->
                        world.setBlock(hitResult.x, hitResult.y, hitResult.z + 1, block)
                    Blocks.FACE_RIGHT ->
                        world.setBlock(hitResult.x + 1, hitResult.y, hitResult.z, block)
                    Blocks.FACE_TOP ->
                        world.setBlock(hitResult.x, hitResult.y + 1, hitResult.z, block)
                    Blocks.FACE_LEFT ->
                        world.setBlock(hitResult.x - 1, hitResult.y, hitResult.z, block)
                    Blocks.FACE_BACK ->
                        world.setBlock(hitResult.x, hitResult.y, hitResult.z - 1, block)
                    Blocks.FACE_BOTTOM ->
                        world.setBlock(hitResult.x, hitResult.y - 1, hitResult.z, block)
                }
            }
        }
    }

    fun update() = Unit

    private fun pick(viewMatrix: Matrix4f) {
        glSelectBuffer(selectBuffer.clear())
        glRenderMode(GL_SELECT)
        world.pick(program, viewMatrix)
        val hits = glRenderMode(GL_RENDER)
        selectBuffer.flip().limit(selectBuffer.capacity())
        var closest = 0L
        val names = intArrayOfSize(10)
        var hitNameCount = 0
        for (i in 0 until hits) {
            val nameCount = selectBuffer.get()
            val minZ = selectBuffer.get().toLong()
            selectBuffer.get()
            if (minZ >= closest && i != 0) {
                for (j in 0 until nameCount)
                    selectBuffer.get()
            } else {
                closest = minZ
                hitNameCount = nameCount
                for (j in 0 until nameCount) {
                    names[j] = selectBuffer.get()
                }
            }
        }
        if (hitNameCount > 0) {
            hitResult.isNull = false
            hitResult.x = names[1]
            hitResult.y = names[2]
            hitResult.z = names[3]
            hitResult.face = names[4]
            hitResult.block = world.getBlock(hitResult.x, hitResult.y, hitResult.z)
        } else hitResult.isNull = true
    }

    fun render(window: Window) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        if (window.resized) {
            crossHair.x = window.width / 2
            crossHair.y = window.height / 2
            blocksTab.x = window.width / 2
            blocksTab.y = window.height / 2
            glViewport(0, 0, window.width, window.height)
            window.resized = false
        }
        with(program) {
            bind()
            val viewMatrix = Transformation.getViewMatrix()
            setUniform("texSampler", 0)
            if (!FreeWorldClient.showTab) {
                setUniform(
                    "projectionMatrix",
                    Transformation.getPickMatrix(window)
                )
                pick(viewMatrix)
            }
            setUniform(
                "projectionMatrix",
                Transformation.getProjectionMatrix(window)
            )
            world.render(this, viewMatrix)
            disableCullFace()
            renderHint(viewMatrix)
        }
        glDisable(GL_DEPTH_TEST)
        renderGui(window)
        enableCullFace()
        glEnable(GL_DEPTH_TEST)
        GlProgram.unbind()
    }

    private fun renderHint(viewMatrix: Matrix4f) {
        program.setUniform(
            "modelViewMatrix",
            Transformation.getModelViewMatrix(hitResult, viewMatrix)
        )
        if (!hitResult.isNull
            && !world.getBlock(
                hitResult.x,
                hitResult.y,
                hitResult.z
            ).getOutlineShape().isNull) {
//            -0.001f, -0.001f, -0.001f,
//            -0.001f, 1.001f, -0.001f,
//            1.001f, 1.001f, -0.001f,
//            1.001f, -0.001f, -0.001f,
//            -0.001f, -0.001f, 1.001f,
//            -0.001f, 1.001f, 1.001f,
//            1.001f, 1.001f, 1.001f,
//            1.001f, -0.001f, 1.001f
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
            for (set in hitResult.block.getOutlineShape().sets) {
                hitResultBox.vertices[0] = set.originX - 0.001f
                hitResultBox.vertices[1] = set.originY - 0.001f
                hitResultBox.vertices[2] = set.originZ - 0.001f

                hitResultBox.vertices[3] = set.originX - 0.001f
                hitResultBox.vertices[4] = set.endY + 0.001f
                hitResultBox.vertices[5] = set.originZ - 0.001f

                hitResultBox.vertices[6] = set.endX + 0.001f
                hitResultBox.vertices[7] = set.endY + 0.001f
                hitResultBox.vertices[8] = set.originZ - 0.001f

                hitResultBox.vertices[9] = set.endX + 0.001f
                hitResultBox.vertices[10] = set.originY - 0.001f
                hitResultBox.vertices[11] = set.originZ - 0.001f

                hitResultBox.vertices[12] = set.originX - 0.001f
                hitResultBox.vertices[13] = set.originY - 0.001f
                hitResultBox.vertices[14] = set.endZ + 0.001f

                hitResultBox.vertices[15] = set.originX - 0.001f
                hitResultBox.vertices[16] = set.endY + 0.001f
                hitResultBox.vertices[17] = set.endZ + 0.001f

                hitResultBox.vertices[18] = set.endX + 0.001f
                hitResultBox.vertices[19] = set.endY + 0.001f
                hitResultBox.vertices[20] = set.endZ + 0.001f

                hitResultBox.vertices[21] = set.endX + 0.001f
                hitResultBox.vertices[22] = set.originY - 0.001f
                hitResultBox.vertices[23] = set.endZ + 0.001f
                hitResultBox.render()
            }
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
        }
    }

    private fun renderGui(window: Window) {
        with(program2D) {
            bind()
            val proj = Transformation.getOrthoProjMatrix(window)
            setUniform("texSampler", 0)
            if (FreeWorldClient.showTab) {
                setUniform(
                    "projModelViewMat",
                    Transformation.getOrthoProjModelMatrix(
                        blocksTab,
                        proj
                    )
                )
                blocksTab.render()
            } else {
                setUniform(
                    "projModelViewMat",
                    Transformation.getOrthoProjModelMatrix(
                        crossHair,
                        proj
                    )
                )
                crossHair.render()
            }
        }
    }

    override fun close() {
        MemoryUtil.memFree(selectBuffer)
        Mesh.closeAll()
        if (this::program.isInitialized) {
            program.close()
            program.disableVertexAttribArrays("vert", "in_color", "in_texCoord")
        }
        if (this::program2D.isInitialized) {
            program2D.close()
            program2D.disableVertexAttribArrays("vert", "in_color", "in_texCoord")
        }
        GlProgram.unbind()
    }
}